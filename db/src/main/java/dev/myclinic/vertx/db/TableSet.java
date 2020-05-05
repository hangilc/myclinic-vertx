package dev.myclinic.vertx.db;

import dev.myclinic.vertx.db.table.*;

public class TableSet {

    public ByoumeiMasterTable byoumeiMasterTable;
    public ChargeTable chargeTable;
    public ConductTable conductTable;
    public ConductDrugTable conductDrugTable;
    public ConductKizaiTable conductKizaiTable;
    public ConductShinryouTable conductShinryouTable;
    public DiseaseTable diseaseTable;
    public DiseaseAdjTable diseaseAdjTable;
    public DrugTable drugTable;
    public DrugAttrTable drugAttrTable;
    public GazouLabelTable gazouLabelTable;
    public HotlineTable hotlineTable;
    public IntraclinicCommentTable intraclinicCommentTable;
    public IntraclinicPostTable intraclinicPostTable;
    public IntraclinicTagTable intraclinicTagTable;
    public IntraclinicTagPostTable intraclinicTagPostTable;
    public IyakuhinMasterTable iyakuhinMasterTable;
    public KizaiMasterTable kizaiMasterTable;
    public KouhiTable kouhiTable;
    public KoukikoureiTable koukikoureiTable;
    public PatientTable patientTable;
    public PaymentTable paymentTable;
    public PharmaDrugTable pharmaDrugTable;
    public PharmaQueueTable pharmaQueueTable;
    public PracticeLogTable practiceLogTable;
    public PrescExampleTable prescExampleTable;
    public RoujinTable roujinTable;
    public ShahokokuhoTable shahokokuhoTable;
    public ShinryouTable shinryouTable;
    public ShinryouAttrTable shinryouAttrTable;
    public ShinryouMasterTable shinryouMasterTable;
    public ShoukiTable shoukiTable;
    public ShuushokugoMasterTable shuushokugoMasterTable;
    public TextTable textTable;
    public VisitTable visitTable;
    public WqueueTable wqueueTable;

    public MysqlDialect dialect;
    
    private TableSet(){}

    public static TableSet create(){
        TableSet ts = new TableSet();
        ts.byoumeiMasterTable = new ByoumeiMasterTable();
        ts.chargeTable = new ChargeTable();
        ts.conductTable = new ConductTable();
        ts.conductDrugTable = new ConductDrugTable();
        ts.conductKizaiTable = new ConductKizaiTable();
        ts.conductShinryouTable = new ConductShinryouTable();
        ts.diseaseTable = new DiseaseTable();
        ts.diseaseAdjTable = new DiseaseAdjTable();
        ts.drugTable = new DrugTable();
        ts.drugAttrTable = new DrugAttrTable();
        ts.gazouLabelTable = new GazouLabelTable();
        ts.hotlineTable = new HotlineTable();
        ts.intraclinicCommentTable = new IntraclinicCommentTable();
        ts.intraclinicPostTable = new IntraclinicPostTable();
        ts.intraclinicTagTable = new IntraclinicTagTable();
        ts.intraclinicTagPostTable = new IntraclinicTagPostTable();
        ts.iyakuhinMasterTable = new IyakuhinMasterTable();
        ts.kizaiMasterTable = new KizaiMasterTable();
        ts.kouhiTable = new KouhiTable();
        ts.koukikoureiTable = new KoukikoureiTable();
        ts.patientTable = new PatientTable();
        ts.paymentTable = new PaymentTable();
        ts.pharmaDrugTable = new PharmaDrugTable();
        ts.pharmaQueueTable = new PharmaQueueTable();
        ts.practiceLogTable = new PracticeLogTable();
        ts.prescExampleTable = new PrescExampleTable();
        ts.roujinTable = new RoujinTable();
        ts.shahokokuhoTable = new ShahokokuhoTable();
        ts.shinryouTable = new ShinryouTable();
        ts.shinryouAttrTable = new ShinryouAttrTable();
        ts.shinryouMasterTable = new ShinryouMasterTable();
        ts.shoukiTable = new ShoukiTable();
        ts.shuushokugoMasterTable = new ShuushokugoMasterTable();
        ts.textTable = new TextTable();
        ts.visitTable = new VisitTable();
        ts.wqueueTable = new WqueueTable();

        ts.dialect = new MysqlDialect();
        return ts;
    }

}
