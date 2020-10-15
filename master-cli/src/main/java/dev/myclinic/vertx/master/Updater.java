package dev.myclinic.vertx.master;

import dev.myclinic.vertx.master.db.DB;
import dev.myclinic.vertx.master.db.ShinryouMaster;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

abstract class Updater<M, C> {

    public abstract String getMasterKind();
    public abstract void iterateCSV(Consumer<C> consumer) throws Exception ;
    public abstract Connection openConnection() throws Exception ;
    public abstract int getMasterCodeOfCSV(C csv);
    public abstract int getHenkouKubunOfCSV(C csv);
    public abstract M getCurrentMaster(Connection conn, int code, LocalDate validFrom) throws Exception ;
    public abstract int getMasterCodeOfMaster(M master);
    public abstract String getNameOfMaster(M master);
    public abstract LocalDate getValidFromOfMaster(M master);
    public abstract int countUsage(Connection conn, int mastercode, LocalDate validFrom) throws Exception ;
    public abstract int deleteMaster(Connection conn, M master) throws Exception;
    public abstract int updateMasterValidUpto(Connection conn, M master, LocalDate validUpto);
    public abstract void insertMaster(Connection conn, C csv, LocalDate validFrom);
    public abstract int updateMasterByCSV(Connection conn, M master, C csv);
    public abstract boolean isEqualMasterAndCSV(M master, C csv);
    public abstract String listDifferences(M oldMaster, C newCSV);

    public void dryRun(LocalDate validFrom) throws Exception {
        innerUpdate(validFrom, true);
    }

    public void update(LocalDate validFrom) throws Exception {
        innerUpdate(validFrom, false);
    }

    private void innerUpdate(LocalDate validFrom, boolean isDryRun) throws Exception {
        LocalDate prev = validFrom.minus(1, ChronoUnit.DAYS);
        Connection conn = openConnection();
        iterateCSV(csv -> {
            try {
                M cur = getCurrentMaster(conn, getMasterCodeOfCSV(csv), validFrom);
                if (getHenkouKubunOfCSV(csv) == MasterConsts.HenkouKubunHaishi) {
                    doHaishi(conn, cur, csv, validFrom, isDryRun);
                } else {
                    if (cur == null) {
                        doNew(conn, csv, validFrom, isDryRun);
                    } else {
                        if (!isEqualMasterAndCSV(cur, csv)) {
                            if( validFrom.equals(getValidFromOfMaster(cur)) ){
                                doUpdateMasterInPlace(conn, cur, csv, isDryRun);
                            } else {
                                if( validFrom.isAfter(LocalDate.parse(cur.validFrom)) ){
                                    invalidateAndEnter(conn, csv, validFrom);
                                    System.out.printf("[MODIFIED](%d) %s\n", csv.shinryoucode, csv.name);
                                    System.out.printf("  old: %s\n", cur.toString());
                                    System.out.printf("  new: %s\n", csv.toString());
                                } else {
                                    throwError("Invalid validFrom %s")
                                }
                            }
                        }
                    }
                }
            } catch (Exception throwables) {
                throwables.printStackTrace(System.err);
                System.exit(1);
            }
        });
        conn.close();
    }

    private void doHaishi(Connection conn, M cur, C csv, LocalDate validFrom, boolean isDryRun) throws Exception {
        if (cur != null) {
            if( getValidFromOfMaster(cur).equals(validFrom) ){
                doHaishiDelete(conn, cur, isDryRun);
            } else {
                if( validFrom.isAfter(getValidFromOfMaster(cur)) ){
                    doHaishiInvalidating(conn, cur, csv, validFrom, isDryRun);
                } else {
                    throwError(String.format("Invalid validFrom (haishi) %s", validFrom.toString()), cur);
                }
            }
        }
    }

    private void doHaishiDelete(Connection conn, M master, boolean isDryRun) throws Exception {
        int n = countUsage(conn, getMasterCodeOfMaster(master), getValidFromOfMaster(master));
        if( n > 0 ){
            throwError("Failed to delete because it is in use", master);
        }
        if( isDryRun ){
            report("DELETE", master);
        } else {
            n = deleteMaster(conn, master);
            if (n > 0) {
                throwError("Unexpected multiple deletion", master);
            }
            report("DELETED", master);
            reportDetail("deleted", master);
        }
    }

    private void doHaishiInvalidating(Connection conn, M cur, C csv, LocalDate validFrom, boolean isDryRun)
            throws Exception {
        int n = countUsage(conn, getMasterCodeOfMaster(cur), validFrom);
        if( n > 0 ){
            throwError("Failed to delete because it is in use", cur);
        }
        if( isDryRun ){
            report(String.format("INVALIDATE at %s", validFrom.toString()), cur);
        } else {
            n = updateMasterValidUpto(conn, cur, prevDay(validFrom));
            if (n != 1) {
                throwError("Failed to update valid_from", cur);
            }
            report(String.format("INVALIDATED at %s", validFrom.toString()), cur));
            reportDetail("invalidated", cur);
        }
    }

    private void doNew(Connection conn, C csv, LocalDate validFrom, boolean isDryRun) throws Exception {
        if( isDryRun ){
            report(String.format("NEW at %s", validFrom.toString()), getMasterCodeOfCSV(csv), "");
            reportDetail("new", csv);
        } else {
            insertMaster(conn, csv, validFrom);
            M master = getCurrentMaster(conn, getMasterCodeOfCSV(csv), validFrom);
            report("ENTERED", master);
            reportDetail("entered", master);
        }
    }

    private void doUpdateMasterInPlace(Connection conn, M master, C csv, boolean isDryRun) throws Exception {
        if( isDryRun ){
            report("UPDATE", master);
            reportDetail("changes", listDifferences(master, csv));
        } else {
            int n = updateMasterByCSV(conn, master, csv);
            if( n != 1 ){
                throwError("Failed to update master in place", master);
            }
        }
    }

    private void throwError(String message, M master){
        String msg = String.format("%s:%s:%d:%s\n",
                getMasterKind(), message, getMasterCodeOfMaster(master), getNameOfMaster(master));
        throw new RuntimeException(msg);
    }

    private void report(String proc, M master){
        report(proc, getMasterCodeOfMaster(master), getNameOfMaster(master));
    }

    private void report(String proc, int code, String name){
        System.out.printf("%s:%s:%d:%s\n", getMasterKind(), proc, code, name);
    }

    private void reportDetail(String title, Object obj){
        System.out.printf("  %s:%s\n", title, obj.toString());
    }

    private LocalDate prevDay(LocalDate day){
        return day.minus(1, ChronoUnit.DAYS);
    }

}
