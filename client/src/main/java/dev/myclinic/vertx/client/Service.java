package dev.myclinic.vertx.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.myclinic.vertx.dto.*;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Service {
    public interface ServerAPI {
        @GET("search-patient")
        CompletableFuture<List<PatientDTO>> searchPatient(@Query("text") String text);

        @GET("search-patient")
        Call<List<PatientDTO>> searchPatientCall(@Query("text") String text);

        @GET("get-patient")
        CompletableFuture<PatientDTO> getPatient(@Query("patient-id") int patientId);

        @GET("get-patient")
        Call<PatientDTO> getPatientCall(@Query("patient-id") int patientId);

        @POST("enter-patient")
        CompletableFuture<Integer> enterPatient(@Body PatientDTO patientDTO);

        @POST("update-patient")
        CompletableFuture<Boolean> updatePatient(@Body PatientDTO patientDTO);

        @GET("list-visit-full2")
        CompletableFuture<VisitFull2PageDTO> listVisitFull2(@Query("patient-id") int patientId, @Query("page") int page);

        @GET("list-visit-full2")
        Call<VisitFull2PageDTO> listVisitFull2Call(@Query("patient-id") int patientId, @Query("page") int page);

        @GET("page-visit-full2-with-patient-at")
        CompletableFuture<VisitFull2PatientPageDTO> pageVisitFullWithPatientAt(@Query("at") String at,
                                                                               @Query("page") int page);

        @GET("page-visit-full2-with-patient-at")
        Call<VisitFull2PatientPageDTO> pageVisitFullWithPatientAtCall(@Query("at") String at,
                                                                      @Query("page") int page);

        @POST("update-text")
        CompletableFuture<Boolean> updateText(@Body TextDTO textDTO);

        @POST("update-text")
        Call<Boolean> updateTextCall(@Body TextDTO textDTO);

        @POST("enter-text")
        CompletableFuture<Integer> enterText(@Body TextDTO textDTO);

        @POST("enter-text")
        Call<Integer> enterTextCall(@Body TextDTO textDTO);

        @POST("delete-text")
        CompletableFuture<Boolean> deleteText(@Query("text-id") int textId);

        @GET("search-text-by-page")
        CompletableFuture<TextVisitPageDTO> searchTextByPage(@Query("patient-id") int patientId,
                                                             @Query("text") String text, @Query("page") int page);

        @GET("search-text-by-page")
        Call<TextVisitPageDTO> searchTextByPageCall(@Query("patient-id") int patientId,
                                                    @Query("text") String text, @Query("page") int page);

        @GET("list-available-hoken")
        CompletableFuture<HokenDTO> listAvailableHoken(@Query("patient-id") int patientId, @Query("at") String at);

        @GET("list-available-hoken")
        Call<HokenDTO> listAvailableHokenCall(@Query("patient-id") int patientId, @Query("at") String at);

        @POST("update-hoken")
        CompletableFuture<Boolean> updateHoken(@Body VisitDTO visitDTO);

        @POST("update-hoken")
        Call<Boolean> updateHokenCall(@Body VisitDTO visitDTO);

        @GET("get-visit")
        CompletableFuture<VisitDTO> getVisit(@Query("visit-id") int visitId);

        @GET("get-visit")
        Call<VisitDTO> getVisitCall(@Query("visit-id") int visitId);

        @GET("get-hoken")
        CompletableFuture<HokenDTO> getHoken(@Query("visit-id") int visitId);

        @GET("get-hoken")
        Call<HokenDTO> getHokenCall(@Query("visit-id") int visitId);

        @POST("convert-to-hoken")
        CompletableFuture<HokenDTO> convertToHoken(@Body VisitDTO visitDTO);

        @POST("convert-to-hoken")
        Call<HokenDTO> convertToHokenCall(@Body VisitDTO visitDTO);

        @GET("search-iyakuhin-master-by-name")
        CompletableFuture<List<IyakuhinMasterDTO>> searchIyakuhinMaster(@Query("text") String text, @Query("at") String at);

        @GET("search-iyakuhin-master-by-name")
        Call<List<IyakuhinMasterDTO>> searchIyakuhinMasterCall(@Query("text") String text, @Query("at") String at);

        @GET("search-presc-example-full-by-name")
        CompletableFuture<List<PrescExampleFullDTO>> searchPrescExample(@Query("text") String text);

        @GET("search-presc-example-full-by-name")
        Call<List<PrescExampleFullDTO>> searchPrescExampleCall(@Query("text") String text);

        @POST("enter-presc-example")
        CompletableFuture<Integer> enterPrescExample(@Body PrescExampleDTO prescExample);

        @POST("update-presc-example")
        CompletableFuture<Boolean> updatePrescExample(@Body PrescExampleDTO prescExample);

        @POST("delete-presc-example")
        CompletableFuture<Boolean> deletePrescExample(@Query("presc-example-id") int prescExampleId);

        @GET("list-all-presc-example")
        CompletableFuture<List<PrescExampleFullDTO>> listAllPrescExample();

        @GET("search-prev-drug")
        CompletableFuture<List<DrugFullDTO>> searchPrevDrug(@Query("text") String text, @Query("patient-id") int patientId);

        @GET("search-prev-drug")
        Call<List<DrugFullDTO>> searchPrevDrugCall(@Query("text") String text, @Query("patient-id") int patientId);

        @GET("resolve-iyakuhin-master")
        CompletableFuture<IyakuhinMasterDTO> resolveIyakuhinMaster(@Query("iyakuhincode") int iyakuhincode,
                                                                   @Query("at") String at);

        @GET("resolve-iyakuhin-master")
        Call<IyakuhinMasterDTO> resolveIyakuhinMasterCall(@Query("iyakuhincode") int iyakuhincode,
                                                          @Query("at") String at);

        @POST("enter-drug")
        CompletableFuture<Integer> enterDrug(@Body DrugDTO drug);

        @POST("enter-drug")
        Call<Integer> enterDrugCall(@Body DrugDTO drug);

        @GET("get-drug")
        CompletableFuture<DrugDTO> getDrug(@Query("drug-id") int drugId);

        @GET("get-drug")
        Call<DrugDTO> getDrugCall(@Query("drug-id") int drugId);

        @GET("get-drug-full")
        CompletableFuture<DrugFullDTO> getDrugFull(@Query("drug-id") int drugId);

        @GET("get-drug-full")
        Call<DrugFullDTO> getDrugFullCall(@Query("drug-id") int drugId);

        @POST("start-visit")
        CompletableFuture<Integer> startVisit(@Query("patient-id") int patientId);

        @POST("start-visit")
        Call<Integer> startVisitCall(@Query("patient-id") int patientId);

        @POST("delete-visit")
        CompletableFuture<Boolean> deleteVisit(@Query("visit-id") int visitId);

        @POST("delete-visit")
        Call<Boolean> deleteVisitCall(@Query("visit-id") int visitId);

        @GET("list-wqueue-full")
        CompletableFuture<List<WqueueFullDTO>> listWqueueFull();

        @GET("list-wqueue-full")
        Call<List<WqueueFullDTO>> listWqueueFullCall();

        @GET("list-wqueue-full-for-exam")
        CompletableFuture<List<WqueueFullDTO>> listWqueueFullForExam();

        @GET("list-wqueue-full-for-exam")
        Call<List<WqueueFullDTO>> listWqueueFullForExamCall();

        @GET("list-visit-with-patient")
        CompletableFuture<List<VisitPatientDTO>> listRecentVisits();

        @GET("list-visit-with-patient")
        Call<List<VisitPatientDTO>> listRecentVisitsCall();

        @GET("list-todays-visits")
        CompletableFuture<List<VisitPatientDTO>> listTodaysVisits();

        @GET("list-todays-visits")
        Call<List<VisitPatientDTO>> listTodaysVisitsCall();

        @POST("start-exam")
        CompletableFuture<Boolean> startExam(@Query("visit-id") int visitId);

        @POST("start-exam")
        Call<Boolean> startExamCall(@Query("visit-id") int visitId);

        @POST("suspend-exam")
        CompletableFuture<Boolean> suspendExam(@Query("visit-id") int visitId);

        @POST("suspend-exam")
        Call<Boolean> suspendExamCall(@Query("visit-id") int visitId);

        @POST("end-exam")
        CompletableFuture<Boolean> endExam(@Query("visit-id") int visitId, @Query("charge") int charge);

        @POST("end-exam")
        Call<Boolean> endExamCall(@Query("visit-id") int visitId, @Query("charge") int charge);

        @GET("get-text")
        CompletableFuture<TextDTO> getText(@Query("text-id") int textId);

        @GET("get-text")
        Call<TextDTO> getTextCall(@Query("text-id") int textId);

        @GET("list-drug-full")
        CompletableFuture<List<DrugFullDTO>> listDrugFull(@Query("visit-id") int visitId);

        @GET("list-drug-full")
        Call<List<DrugFullDTO>> listDrugFullCall(@Query("visit-id") int visitId);

        @GET("list-drug-full-by-drug-ids")
        CompletableFuture<List<DrugFullDTO>> listDrugFullByDrugIds(@Query("drug-id") List<Integer> drugIds);

        @GET("list-drug-full-by-drug-ids")
        Call<List<DrugFullDTO>> listDrugFullByDrugIdsCall(@Query("drug-id") List<Integer> drugIds);

        @GET("batch-resolve-iyakuhin-master")
        CompletableFuture<Map<Integer, IyakuhinMasterDTO>> batchResolveIyakuhinMaster(@Query("iyakuhincode") List<Integer> iyakuhincodes,
                                                                                      @Query("at") String at);

        @GET("batch-resolve-iyakuhin-master")
        Call<Map<Integer, IyakuhinMasterDTO>> batchResolveIyakuhinMasterCall(@Query("iyakuhincode") List<Integer> iyakuhincodes,
                                                                             @Query("at") String at);

        @POST("batch-enter-drugs")
        CompletableFuture<List<Integer>> batchEnterDrugs(@Body List<DrugDTO> drugs);

        @POST("batch-enter-drugs")
        Call<List<Integer>> batchEnterDrugsCall(@Body List<DrugDTO> drugs);

        @POST("batch-update-drug-days")
        CompletableFuture<Boolean> batchUpdateDrugDays(@Query("drug-id") List<Integer> drugIds, @Query("days") int days);

        @POST("batch-update-drug-days")
        Call<Boolean> batchUpdateDrugDaysCall(@Query("drug-id") List<Integer> drugIds, @Query("days") int days);

        @POST("batch-delete-drugs")
        CompletableFuture<Boolean> batchDeleteDrugs(@Query("drug-id") List<Integer> drugIds);

        @POST("batch-delete-drugs")
        Call<Boolean> batchDeleteDrugsCall(@Query("drug-id") List<Integer> drugIds);

        @POST("delete-drug")
        CompletableFuture<Boolean> deleteDrug(@Query("drug-id") int drugId);

        @POST("delete-drug")
        Call<Boolean> deleteDrugCall(@Query("drug-id") int drugId);

        @POST("update-drug")
        CompletableFuture<Boolean> updateDrug(@Body DrugDTO drug);

        @POST("update-drug")
        Call<Boolean> updateDrugCall(@Body DrugDTO drug);

        @POST("batch-enter-shinryou-by-name")
        CompletableFuture<BatchEnterResultDTO> batchEnterShinryouByName(@Query("name") List<String> names,
                                                                        @Query("visit-id") int visitId);

        @POST("batch-enter-shinryou-by-name")
        Call<BatchEnterResultDTO> batchEnterShinryouByNameCall(@Query("name") List<String> names,
                                                               @Query("visit-id") int visitId);

        @POST("enter-shinryou")
        CompletableFuture<Integer> enterShinryou(@Body ShinryouDTO shinryou);

        @POST("enter-shinryou")
        Call<Integer> enterShinryouCall(@Body ShinryouDTO shinryou);

        @POST("update-shinryou")
        CompletableFuture<Boolean> updateShinryou(@Body ShinryouDTO shinryou);

        @POST("update-shinryou")
        Call<Boolean> updateShinryouCall(@Body ShinryouDTO shinryou);

        @POST("delete-shinryou")
        CompletableFuture<Boolean> deleteShinryou(@Query("shinryou-id") int shinryouId);

        @POST("delete-shinryou")
        Call<Boolean> deleteShinryouCall(@Query("shinryou-id") int shinryouId);

        @GET("list-shinryou-full-by-ids")
        CompletableFuture<List<ShinryouFullDTO>> listShinryouFullByIds(@Query("shinryou-id") List<Integer> shinryouIds);

        @GET("list-shinryou-full-by-ids")
        Call<List<ShinryouFullDTO>> listShinryouFullByIdsCall(@Query("shinryou-id") List<Integer> shinryouIds);

        @GET("list-shinryou-full")
        CompletableFuture<List<ShinryouFullDTO>> listShinryouFull(@Query("visit-id") int visitId);

        @GET("list-shinryou-full")
        Call<List<ShinryouFullDTO>> listShinryouFullCall(@Query("visit-id") int visitId);

        @GET("list-conduct-full-by-ids")
        CompletableFuture<List<ConductFullDTO>> listConductFullByIds(@Query("conduct-id") List<Integer> conductIds);

        @GET("list-conduct-full-by-ids")
        Call<List<ConductFullDTO>> listConductFullByIdsCall(@Query("conduct-id") List<Integer> conductIds);

        @GET("search-shinryou-master")
        CompletableFuture<List<ShinryouMasterDTO>> searchShinryouMaster(@Query("text") String text, @Query("at") String at);

        @GET("search-shinryou-master")
        Call<List<ShinryouMasterDTO>> searchShinryouMasterCall(@Query("text") String text, @Query("at") String at);

        @GET("resolve-shinryoucode")
        CompletableFuture<Integer> resolveShinryoucode(@Query("shinryoucode") int shinryoucode, @Query("at") String at);

        @GET("resolve-shinryoucode")
        Call<Integer> resolveShinryoucodeCall(@Query("shinryoucode") int shinryoucode, @Query("at") String at);

        @GET("resolve-shinryou-master-by-name")
        CompletableFuture<ShinryouMasterDTO> resolveShinryouMasterByName(@Query("name") String name, @Query("at") String at);

        @GET("resolve-shinryou-master-by-name")
        Call<ShinryouMasterDTO> resolveShinryouMasterByNameCall(@Query("name") String name, @Query("at") String at);

        @GET("resolve-shinryou-master")
        CompletableFuture<ShinryouMasterDTO> resolveShinryouMaster(@Query("shinryoucode") int shinryoucode,
                                                                   @Query("at") String at);

        @GET("resolve-shinryou-master")
        Call<ShinryouMasterDTO> resolveShinryouMasterCall(@Query("shinryoucode") int shinryoucode,
                                                          @Query("at") String at);

        @GET("resolve-kizai-master-by-name")
        CompletableFuture<KizaiMasterDTO> resolveKizaiMasterByName(@Query("name") String name, @Query("at") String at);

        @GET("resolve-kizai-master-by-name")
        Call<KizaiMasterDTO> resolveKizaiMasterByNameCall(@Query("name") String name, @Query("at") String at);

        @GET("resolve-kizai-master")
        CompletableFuture<KizaiMasterDTO> resolveKizaiMaster(@Query("kizaicode") int kizaicode,
                                                             @Query("at") String at);

        @GET("resolve-kizai-master")
        Call<KizaiMasterDTO> resolveKizaiMasterCall(@Query("kizaicode") int kizaicode,
                                                    @Query("at") String at);

        @GET("get-shinryou-master")
        CompletableFuture<ShinryouMasterDTO> getShinryouMaster(@Query("shinryoucode") int shinryoucode, @Query("at") String at);

        @GET("get-shinryou-master")
        Call<ShinryouMasterDTO> getShinryouMasterCall(@Query("shinryoucode") int shinryoucode, @Query("at") String at);

        @GET("get-shinryou-full")
        CompletableFuture<ShinryouFullDTO> getShinryouFull(@Query("shinryou-id") int shinryouId);

        @GET("get-shinryou-full")
        Call<ShinryouFullDTO> getShinryouFullCall(@Query("shinryou-id") int shinryouId);

        @POST("batch-delete-shinryou")
        CompletableFuture<Boolean> batchDeleteShinryou(@Query("shinryou-id") List<Integer> shinryouIds);

        @POST("batch-delete-shinryou")
        Call<Boolean> batchDeleteShinryouCall(@Query("shinryou-id") List<Integer> shinryouIds);

        @POST("batch-copy-shinryou")
        CompletableFuture<List<Integer>> batchCopyShinryou(@Query("visit-id") int visitId,
                                                           @Body List<ShinryouDTO> srcList);

        @POST("batch-copy-shinryou")
        Call<List<Integer>> batchCopyShinryouCall(@Query("visit-id") int visitId,
                                                  @Body List<ShinryouDTO> srcList);

        @POST("delete-duplicate-shinryou")
        CompletableFuture<List<Integer>> deleteDuplicateShinryou(@Query("visit-id") int visitId);

        @POST("delete-duplicate-shinryou")
        Call<List<Integer>> deleteDuplicateShinryouCall(@Query("visit-id") int visitId);

        @GET("get-conduct")
        CompletableFuture<ConductDTO> getConduct(@Query("conduct-id") int conductId);

        @GET("get-conduct")
        Call<ConductDTO> getConductCall(@Query("conduct-id") int conductId);

        @GET("get-find-label")
        CompletableFuture<GazouLabelDTO> findGazouLabel(@Query("conduct-id") int conductId);

        @GET("get-find-label")
        Call<GazouLabelDTO> findGazouLabelCall(@Query("conduct-id") int conductId);

        @GET("get-conduct-full")
        CompletableFuture<ConductFullDTO> getConductFull(@Query("conduct-id") int conductId);

        @GET("get-conduct-full")
        Call<ConductFullDTO> getConductFullCall(@Query("conduct-id") int conductId);

        @POST("enter-conduct-full")
        CompletableFuture<ConductFullDTO> enterConductFull(@Body ConductEnterRequestDTO arg);

        @POST("enter-conduct-full")
        Call<ConductFullDTO> enterConductFullCall(@Body ConductEnterRequestDTO arg);

        @POST("enter-xp")
        CompletableFuture<Integer> enterXp(@Query("visit-id") int visitId, @Query("label") String label, @Query("film") String film);

        @POST("enter-xp")
        Call<Integer> enterXpCall(@Query("visit-id") int visitId, @Query("label") String label, @Query("film") String film);

        @POST("enter-inject")
        CompletableFuture<Integer> enterInject(@Query("visit-id") int visitId,
                                               @Query("kind") int conductKindCode,
                                               @Query("iyakuhincode") int iyakuhincode,
                                               @Query("amount") double amount);

        @POST("enter-inject")
        Call<Integer> enterInjectCall(@Query("visit-id") int visitId,
                                      @Query("kind") int conductKindCode,
                                      @Query("iyakuhincode") int iyakuhincode,
                                      @Query("amount") double amount);

        @POST("copy-all-conducts")
        CompletableFuture<List<Integer>> copyAllConducts(@Query("target-visit-id") int targetVisitId,
                                                         @Query("source-visit-id") int sourceVisitId);

        @POST("copy-all-conducts")
        Call<List<Integer>> copyAllConductsCall(@Query("target-visit-id") int targetVisitId,
                                                @Query("source-visit-id") int sourceVisitId);

        @POST("delete-conduct")
        CompletableFuture<Boolean> deleteConduct(@Query("conduct-id") int conductId);

        @POST("delete-conduct")
        Call<Boolean> deleteConductCall(@Query("conduct-id") int conductId);

        @POST("enter-conduct-shinryou")
        CompletableFuture<Integer> enterConductShinryou(@Body ConductShinryouDTO conductShinryou);

        @POST("enter-conduct-shinryou")
        Call<Integer> enterConductShinryouCall(@Body ConductShinryouDTO conductShinryou);

        @GET("get-conduct-shinryou-full")
        CompletableFuture<ConductShinryouFullDTO> getConductShinryouFull(@Query("conduct-shinryou-id") int conductShinryouId);

        @GET("get-conduct-shinryou-full")
        Call<ConductShinryouFullDTO> getConductShinryouFullCall(@Query("conduct-shinryou-id") int conductShinryouId);

        @POST("enter-conduct-drug")
        CompletableFuture<Integer> enterConductDrug(@Body ConductDrugDTO conductDrug);

        @POST("enter-conduct-drug")
        Call<Integer> enterConductDrugCall(@Body ConductDrugDTO conductDrug);

        @GET("get-conduct-drug-full")
        CompletableFuture<ConductDrugFullDTO> getConductDrugFull(@Query("conduct-drug-id") int conductDrugId);

        @GET("get-conduct-drug-full")
        Call<ConductDrugFullDTO> getConductDrugFullCall(@Query("conduct-drug-id") int conductDrugId);

        @POST("enter-conduct-kizai")
        CompletableFuture<Integer> enterConductKizai(@Body ConductKizaiDTO conductKizai);

        @POST("enter-conduct-kizai")
        Call<Integer> enterConductKizaiCall(@Body ConductKizaiDTO conductKizai);

        @GET("get-conduct-kizai-full")
        CompletableFuture<ConductKizaiFullDTO> getConductKizaiFull(@Query("conduct-kizai-id") int conductKizaiId);

        @GET("get-conduct-kizai-full")
        Call<ConductKizaiFullDTO> getConductKizaiFullCall(@Query("conduct-kizai-id") int conductKizaiId);

        @GET("search-kizai-master-by-name")
        CompletableFuture<List<KizaiMasterDTO>> searchKizaiMaster(@Query("text") String text, @Query("at") String at);

        @GET("search-kizai-master-by-name")
        Call<List<KizaiMasterDTO>> searchKizaiMasterCall(@Query("text") String text, @Query("at") String at);

        @POST("modify-conduct-kind")
        CompletableFuture<Boolean> modifyConductKind(@Query("conduct-id") int conductId,
                                                     @Query("kind") int kind);

        @POST("modify-conduct-kind")
        Call<Boolean> modifyConductKindCall(@Query("conduct-id") int conductId,
                                            @Query("kind") int kind);

        @POST("modify-gazou-label")
        CompletableFuture<Boolean> modifyGazouLabel(@Query("conduct-id") int conductId,
                                                    @Query("label") String label);

        @POST("modify-gazou-label")
        Call<Boolean> modifyGazouLabelCall(@Query("conduct-id") int conductId,
                                           @Query("label") String label);

        @POST("delete-gazou-label")
        CompletableFuture<Boolean> deleteGazouLabel(@Query("conduct-id") int conductId);

        @POST("delete-gazou-label")
        Call<Boolean> deleteGazouLabelCall(@Query("conduct-id") int conductId);

        @POST("delete-conduct-shinryou")
        CompletableFuture<Boolean> deleteConductShinryou(@Query("conduct-shinryou-id") int conductShinryouId);

        @POST("delete-conduct-shinryou")
        Call<Boolean> deleteConductShinryouCall(@Query("conduct-shinryou-id") int conductShinryouId);

        @POST("delete-conduct-drug")
        CompletableFuture<Boolean> deleteConductDrug(@Query("conduct-drug-id") int conductDrugId);

        @POST("delete-conduct-drug")
        Call<Boolean> deleteConductDrugCall(@Query("conduct-drug-id") int conductDrugId);

        @POST("delete-conduct-kizai")
        CompletableFuture<Boolean> deleteConductKizai(@Query("conduct-kizai-id") int conductKizaiId);

        @POST("delete-conduct-kizai")
        Call<Boolean> deleteConductKizaiCall(@Query("conduct-kizai-id") int conductKizaiId);

        @POST("modify-charge")
        CompletableFuture<Boolean> modifyCharge(@Query("visit-id") int visitId, @Query("charge") int charge);

        @POST("modify-charge")
        Call<Boolean> modifyChargeCall(@Query("visit-id") int visitId, @Query("charge") int charge);

        @GET("get-visit-meisai")
        CompletableFuture<MeisaiDTO> getMeisai(@Query("visit-id") int visitId);

        @GET("get-visit-meisai")
        Call<MeisaiDTO> getMeisaiCall(@Query("visit-id") int visitId);

        @GET("list-current-disease-full")
        CompletableFuture<List<DiseaseFullDTO>> listCurrentDiseaseFull(@Query("patient-id") int patientId);

        @GET("list-current-disease-full")
        Call<List<DiseaseFullDTO>> listCurrentDiseaseFullCall(@Query("patient-id") int patientId);

        @GET("list-disease-full")
        CompletableFuture<List<DiseaseFullDTO>> listDiseaseFull(@Query("patient-id") int patientId);

        @GET("list-disease-full")
        Call<List<DiseaseFullDTO>> listDiseaseFullCall(@Query("patient-id") int patientId);

        @GET("count-page-of-disease-by-patient")
        CompletableFuture<Integer> countPageOfDiseaseByPatient(@Query("patient-id") int patientId,
                                                               @Query("items-per-page") int itemsPerPage);

        @GET("count-page-of-disease-by-patient")
        Call<Integer> countPageOfDiseaseByPatientCall(@Query("patient-id") int patientId,
                                                      @Query("items-per-page") int itemsPerPage);

        @GET("page-disease-full")
        CompletableFuture<List<DiseaseFullDTO>> pageDiseaseFull(@Query("patient-id") int patientId,
                                                                @Query("page") int page,
                                                                @Query("items-per-page") int itemsPerPage);

        @GET("page-disease-full")
        Call<List<DiseaseFullDTO>> pageDiseaseFullCall(@Query("patient-id") int patientId,
                                                       @Query("page") int page,
                                                       @Query("items-per-page") int itemsPerPage);

        @GET("get-disease-full")
        CompletableFuture<DiseaseFullDTO> getDiseaseFull(@Query("disease-id") int diseaseId);

        @GET("get-disease-full")
        Call<DiseaseFullDTO> getDiseaseFullCall(@Query("disease-id") int diseaseId);

        @GET("search-byoumei-master")
        CompletableFuture<List<ByoumeiMasterDTO>> searchByoumei(@Query("text") String text,
                                                                @Query("at") String at);

        @GET("search-byoumei-master")
        Call<List<ByoumeiMasterDTO>> searchByoumeiCall(@Query("text") String text,
                                                       @Query("at") String at);

        @GET("find-byoumei-master-by-name")
        CompletableFuture<ByoumeiMasterDTO> findByoumeiMasterByName(@Query("name") String name,
                                                                    @Query("at") String at);

        @GET("find-byoumei-master-by-name")
        Call<ByoumeiMasterDTO> findByoumeiMasterByNameCall(@Query("name") String name,
                                                           @Query("at") String at);

        @GET("search-shuushokugo-master")
        CompletableFuture<List<ShuushokugoMasterDTO>> searchShuushokugo(@Query("text") String text);

        @GET("search-shuushokugo-master")
        Call<List<ShuushokugoMasterDTO>> searchShuushokugoCall(@Query("text") String text);

        @GET("find-shuushokugo-master-by-name")
        CompletableFuture<ShuushokugoMasterDTO> findShuushokugoMasterByName(@Query("name") String name);

        @GET("find-shuushokugo-master-by-name")
        Call<ShuushokugoMasterDTO> findShuushokugoMasterByNameCall(@Query("name") String name);

        @POST("enter-disease")
        CompletableFuture<Integer> enterDisease(@Body DiseaseNewDTO diseaseNew);

        @POST("enter-disease")
        Call<Integer> enterDiseaseCall(@Body DiseaseNewDTO diseaseNew);

        @GET("list-disease-example")
        CompletableFuture<List<DiseaseExampleDTO>> listDiseaseExample();

        @GET("list-disease-example")
        Call<List<DiseaseExampleDTO>> listDiseaseExampleCall();

        @POST("batch-update-disease-end-reason")
        CompletableFuture<Boolean> batchUpdateDiseaseEndReason(@Body List<DiseaseModifyEndReasonDTO> args);

        @POST("batch-update-disease-end-reason")
        Call<Boolean> batchUpdateDiseaseEndReasonCall(@Body List<DiseaseModifyEndReasonDTO> args);

        @POST("modify-disease")
        CompletableFuture<Boolean> modifyDisease(@Body DiseaseModifyDTO diseaseModifyDTO);

        @POST("modify-disease")
        Call<Boolean> modifyDiseaseCall(@Body DiseaseModifyDTO diseaseModifyDTO);

        @POST("delete-disease")
        CompletableFuture<Boolean> deleteDisease(@Query("disease-id") int diseaseId);

        @POST("delete-disease")
        Call<Boolean> deleteDiseaseCall(@Query("disease-id") int diseaseId);

        @GET("get-clinic-info")
        CompletableFuture<ClinicInfoDTO> getClinicInfo();

        @GET("get-clinic-info")
        Call<ClinicInfoDTO> getClinicInfoCall();

        @GET("get-refer-list")
        CompletableFuture<List<ReferItemDTO>> getReferList();

        @GET("get-refer-list")
        Call<List<ReferItemDTO>> getReferListCall();

        @GET("get-practice-config")
        CompletableFuture<PracticeConfigDTO> getPracticeConfig();

        @GET("get-practice-config")
        Call<PracticeConfigDTO> getPracticeConfigCall();

        @GET("list-visit-charge-patient-at")
        CompletableFuture<List<VisitChargePatientDTO>> listVisitChargePatientAt(@Query("at") String at);

        @GET("list-visit-charge-patient-at")
        Call<List<VisitChargePatientDTO>> listVisitChargePatientAtCall(@Query("at") String at);

        @GET("list-visiting-patient-id-having-hoken")
        CompletableFuture<List<Integer>> listVisitingPatientIdHavingHoken(@Query("year") int year,
                                                                          @Query("month") int month);

        @GET("list-visiting-patient-id-having-hoken")
        Call<List<Integer>> listVisitingPatientIdHavingHokenCall(@Query("year") int year,
                                                                 @Query("month") int month);

        @GET("list-visit-by-patient-having-hoken")
        CompletableFuture<List<VisitFull2DTO>> listVisitByPatientHavingHoken(
                @Query("patient-id") int patientId, @Query("year") int year, @Query("month") int month);

        @GET("list-visit-by-patient-having-hoken")
        Call<List<VisitFull2DTO>> listVisitByPatientHavingHokenCall(
                @Query("patient-id") int patientId, @Query("year") int year, @Query("month") int month);

        @GET("list-disease-by-patient-at")
        CompletableFuture<List<DiseaseFullDTO>> listDiseaseByPatientAt(
                @Query("patient-id") int patientId, @Query("year") int year, @Query("month") int month);

        @GET("list-disease-by-patient-at")
        Call<List<DiseaseFullDTO>> listDiseaseByPatientAtCall(
                @Query("patient-id") int patientId, @Query("year") int year, @Query("month") int month);

        @GET("find-shinryou-master-by-name")
        CompletableFuture<ShinryouMasterDTO> findShinryouMasterByName(@Query("name") String name,
                                                                      @Query("at") String at);

        @GET("find-shinryou-master-by-name")
        Call<ShinryouMasterDTO> findShinryouMasterByNameCall(@Query("name") String name,
                                                             @Query("at") String at);

        @GET("list-all-practice-log")
        CompletableFuture<List<PracticeLogDTO>> listAllPracticeLog(@Query("date") String date);

        @GET("list-all-practice-log")
        Call<List<PracticeLogDTO>> listAllPracticeLogCall(@Query("date") String date);

        @GET("list-practice-log-after")
        CompletableFuture<List<PracticeLogDTO>> listAllPracticeLog(@Query("date") String date,
                                                              @Query("last-id") int lastId);

        @GET("list-practice-log-after")
        Call<List<PracticeLogDTO>> listAllPracticeLogCall(@Query("date") String date,
                                                     @Query("last-id") int lastId);
        @GET("list-practice-log-in-range")
        CompletableFuture<List<PracticeLogDTO>> listPracticeLogInRange(
                @Query("date") String date, @Query("after-id") int afterId,
                @Query("before-id") int beforeId);

        @GET("list-practice-log-in-range")
        Call<List<PracticeLogDTO>> listPracticeLogInRangeCall(
                @Query("date") String date, @Query("after-id") int afterId,
                @Query("before-id") int beforeId);

        @GET("get-shahokokuho")
        CompletableFuture<ShahokokuhoDTO> getShahokokuho(@Query("shahokokuho-id") int shahokokuhoId);

        @GET("get-shahokokuho")
        Call<ShahokokuhoDTO> getShahokokuhoCall(@Query("shahokokuho-id") int shahokokuhoId);

        @GET("get-koukikourei")
        CompletableFuture<KoukikoureiDTO> getKoukikourei(@Query("koukikourei-id") int koukikoureiId);

        @GET("get-koukikourei")
        Call<KoukikoureiDTO> getKoukikoureiCall(@Query("koukikourei-id") int koukikoureiId);

        @GET("get-roujin")
        CompletableFuture<RoujinDTO> getRoujin(@Query("roujin-id") int roujinId);

        @GET("get-roujin")
        Call<RoujinDTO> getRoujinCall(@Query("roujin-id") int roujinId);

        @GET("get-kouhi")
        CompletableFuture<KouhiDTO> getKouhi(@Query("kouhi-id") int kouhiId);

        @GET("get-kouhi")
        Call<KouhiDTO> getKouhiCall(@Query("kouhi-id") int kouhiId);

        @GET("list-visit-text-drug-for-patient")
        CompletableFuture<VisitTextDrugPageDTO> listVisitTextDrugForPatient(@Query("patient-id") int patientId,
                                                                            @Query("page") int page);

        @GET("list-visit-text-drug-by-patient-and-iyakuhincode")
        CompletableFuture<VisitTextDrugPageDTO> listVisitTextDrugByPatientAndIyakuhincode(
                @Query("patient-id") int patientId, @Query("iyakuhincode") int iyakuhincode,
                @Query("page") int page
        );

        @GET("list-iyakuhin-for-patient")
        CompletableFuture<List<IyakuhincodeNameDTO>> listIyakuhinForPatient(@Query("patient-id") int patientId);

        @GET("list-visit-id-visited-at-by-patient-and-iyakuhincode")
        CompletableFuture<List<VisitIdVisitedAtDTO>> listVisitIdVisitedAtByPatientAndIyakuhincode(@Query("patient-id") int patientId,
                                                                                                  @Query("iyakuhincode") int iyakuhincode);

        @GET("find-pharma-drug")
        CompletableFuture<PharmaDrugDTO> findPharmaDrug(@Query("iyakuhincode") int iyakuhincode);

        @GET("find-pharma-drug")
        Call<PharmaDrugDTO> findPharmaDrugCall(@Query("iyakuhincode") int iyakuhincode);

        @GET("list-pharma-queue-full-for-prescription")
        CompletableFuture<List<PharmaQueueFullDTO>> listPharmaQueueForPrescription();

        @GET("list-pharma-queue-full-for-today")
        CompletableFuture<List<PharmaQueueFullDTO>> listPharmaQueueForToday();

        @GET("get-pharma-queue-full")
        CompletableFuture<PharmaQueueFullDTO> getPharmaQueueFull(@Query("visit-id") int visitId);

        @POST("presc-done")
        CompletableFuture<Boolean> prescDone(@Query("visit-id") int visitId);

        @GET("get-pharma-drug")
        CompletableFuture<PharmaDrugDTO> getPharmaDrug(@Query("iyakuhincode") int iyakuhincode);

        @POST("update-pharma-drug")
        CompletableFuture<Boolean> updatePharmaDrug(@Body PharmaDrugDTO pharmaDrugDTO);

        @POST("delete-pharma-drug")
        CompletableFuture<Boolean> deletePharmaDrug(@Query("iyakuhincode") int iyakuhincode);

        @GET("search-iyakuhin-master-by-name")
        CompletableFuture<List<IyakuhinMasterDTO>> searchIyakuhinMasterByName(@Query("text") String text, @Query("at") String at);

        @GET("get-name-of-iyakuhin")
        CompletableFuture<String> getNameOfIyakuhin(@Query("iyakuhincode") int iyakuhincode);

        @POST("enter-pharma-drug")
        CompletableFuture<Boolean> enterPharmaDrug(@Body PharmaDrugDTO pharmaDrugDTO);

        @GET("list-all-pharma-drug-names")
        CompletableFuture<List<PharmaDrugNameDTO>> listAllPharmaDrugNames();

        @GET("page-visit-drug")
        CompletableFuture<VisitDrugPageDTO> pageVisitDrug(@Query("patient-id") int patientId,
                                                          @Query("page") int page);
        @GET("get-visit-meisai")
        CompletableFuture<MeisaiDTO> getVisitMeisai(@Query("visit-id") int visitId);

        @GET("list-payment")
        CompletableFuture<List<PaymentDTO>> listPayment(@Query("visit-id") int visitId);

        @GET("get-charge")
        CompletableFuture<ChargeDTO> getCharge(@Query("visit-id") int visitId);

        @GET("list-hoken")
        CompletableFuture<HokenListDTO> listHoken(@Query("patient-id") int patientId);

        @POST("update-shahokokuho")
        CompletableFuture<Boolean> updateShahokokuho(@Body ShahokokuhoDTO shahokokuhoDTO);

        @POST("update-koukikourei")
        CompletableFuture<Boolean> updateKoukikourei(@Body KoukikoureiDTO koukikoureiDTO);

        @POST("update-kouhi")
        CompletableFuture<Boolean> updateKouhi(@Body KouhiDTO kouhiDTO);

        @POST("enter-shahokokuho")
        CompletableFuture<Integer> enterShahokokuho(@Body ShahokokuhoDTO shahokokuhoDTO);

        @POST("enter-koukikourei")
        CompletableFuture<Integer> enterKoukikourei(@Body KoukikoureiDTO koukikoureiDTO);

        @POST("enter-kouhi")
        CompletableFuture<Integer> enterKouhi(@Body KouhiDTO kouhiDTO);

        @POST("delete-shahokokuho")
        CompletableFuture<Boolean> deleteShahokokuho(@Body ShahokokuhoDTO shahokokuhoDTO);

        @POST("delete-koukikourei")
        CompletableFuture<Boolean> deleteKoukikourei(@Body KoukikoureiDTO koukikoureiDTO);

        @POST("delete-roujin")
        CompletableFuture<Boolean> deleteRoujin(@Body RoujinDTO roujinDTO);

        @POST("delete-kouhi")
        CompletableFuture<Boolean> deleteKouhi(@Body KouhiDTO kouhiDTO);

        @GET("list-payment-by-patient")
        CompletableFuture<List<PaymentVisitPatientDTO>> listPaymentByPatient(@Query("patient-id") int patientId,
                                                                             @Query("n") int n);

        @GET("list-recent-payment")
        CompletableFuture<List<PaymentVisitPatientDTO>> listRecentPayment(@Query("n") int n);

        @POST("finish-cashier")
        CompletableFuture<Boolean> finishCashier(@Body PaymentDTO payment);

        @POST("delete-visit-from-reception")
        CompletableFuture<Boolean> deleteVisitFromReception(@Query("visit-id") int visitId);

//        @GET("list-wqueue-full")
//        CompletableFuture<List<WqueueFullDTO>> listWqueue();

        @GET("get-wqueue-full")
        CompletableFuture<WqueueFullDTO> getWqueueFull(@Query("visit-id") int visitId);

        @GET("list-recently-registered-patients")
        CompletableFuture<List<PatientDTO>> listRecentlyRegisteredPatients();

        @GET("list-hokensho")
        CompletableFuture<List<String>> listHokensho(@Query("patient-id") int patientId);

        @GET("get-hokensho")
        @Streaming
        CompletableFuture<ResponseBody> getHokensho(@Query("patient-id") int patientId, @Query("file") String file);

        @GET("get-hokensho")
        @Streaming
        Call<ResponseBody> getHokenshoCall(@Query("patient-id") int patientId, @Query("file") String file);

        @GET("batch-get-shinryou-attr")
        CompletableFuture<List<ShinryouAttrDTO>> batchGetShinryouAttr(@Query("shinryou-ids") List<Integer> shinryouIds);

        @GET("batch-get-shinryou-attr")
        Call<List<ShinryouAttrDTO>> batchGetShinryouAttrCall(@Query("shinryou-ids") List<Integer> shinryouIds);

        @GET("find-shinryou-attr")
        CompletableFuture<ShinryouAttrDTO> findShinryouAttr(@Query("shinryou-id") int shinryouId);

        @GET("find-shinryou-attr")
        Call<ShinryouAttrDTO> findShinryouAttrCall(@Query("shinryou-id") int shinryouId);

        @POST("set-shinryou-tekiyou")
        CompletableFuture<ShinryouAttrDTO> setShinryouTekiyou(@Query("shinryou-id") int shinryouId,
                                                              @Query("tekiyou") String tekiyou);

        @POST("set-shinryou-tekiyou")
        Call<ShinryouAttrDTO> setShinryouTekiyouCall(@Query("shinryou-id") int shinryouId,
                                                              @Query("tekiyou") String tekiyou);

        @POST("delete-shinryou-tekiyou")
        CompletableFuture<ShinryouAttrDTO> deleteShinryouTekiyou(@Query("shinryou-id") int shinryouId);

        @POST("delete-shinryou-tekiyou")
        Call<ShinryouAttrDTO> deleteShinryouTekiyouCall(@Query("shinryou-id") int shinryouId);

        @POST("enter-shinryou-attr")
        CompletableFuture<Boolean> enterShinryouAttr(@Body() ShinryouAttrDTO attr);

        @POST("enter-shinryou-attr")
        Call<Boolean> enterShinryouAttrCall(@Body() ShinryouAttrDTO attr);

        @GET("batch-get-drug-attr")
        CompletableFuture<List<DrugAttrDTO>> batchGetDrugAttr(@Query("drug-ids") List<Integer> drugIds);

        @GET("batch-get-drug-attr")
        Call<List<DrugAttrDTO>> batchGetDrugAttrCall(@Query("drug-ids") List<Integer> drugIds);

        @GET("find-drug-attr")
        CompletableFuture<DrugAttrDTO> findDrugAttr(@Query("drug-id") int drugId);

        @GET("find-drug-attr")
        Call<DrugAttrDTO> findDrugAttrCall(@Query("drug-id") int drugId);

        @POST("set-drug-tekiyou")
        CompletableFuture<DrugAttrDTO> setDrugTekiyou(@Query("drug-id") int drugId,
                                                              @Query("tekiyou") String tekiyou);

        @POST("set-drug-tekiyou")
        Call<DrugAttrDTO> setDrugTekiyouCall(@Query("drug-id") int drugId,
                                                              @Query("tekiyou") String tekiyou);

        @POST("delete-drug-tekiyou")
        CompletableFuture<DrugAttrDTO> deleteDrugTekiyou(@Query("drug-id") int drugId);

        @POST("delete-drug-tekiyou")
        Call<DrugAttrDTO> deleteDrugTekiyouCall(@Query("drug-id") int drugId);

        @POST("enter-drug-attr")
        CompletableFuture<Boolean> enterDrugAttr(@Body() DrugAttrDTO attr);

        @POST("enter-drug-attr")
        Call<Boolean> enterDrugAttrCall(@Body() DrugAttrDTO attr);

        @GET("batchy-get-shouki")
        CompletableFuture<List<ShoukiDTO>> batchGetShouki(@Query("visit-ids") List<Integer> visitIds);

        @GET("batchy-get-shouki")
        Call<List<ShoukiDTO>> batchGetShoukiCall(@Query("visit-ids") List<Integer> visitIds);

        @GET("find-shouki")
        CompletableFuture<ShoukiDTO> findShouki(@Query("visit-id") int visitId);

        @GET("find-shouki")
        Call<ShoukiDTO> findShoukiCall(@Query("visit-id") int visitId);

        @POST("enter-shouki")
        CompletableFuture<Boolean> enterShouki(@Body ShoukiDTO shouki);

        @POST("enter-shouki")
        Call<Boolean> enterShoukiCall(@Body ShoukiDTO shouki);

        @POST("update-shouki")
        CompletableFuture<Boolean> updateShouki(@Body ShoukiDTO shouki);

        @POST("update-shouki")
        Call<Boolean> updateShoukiCall(@Body ShoukiDTO shouki);

        @POST("delete-shouki")
        CompletableFuture<Boolean> deleteShouki(@Query("visit-id") int visitId);

        @POST("delete-shouki")
        Call<Boolean> deleteShoukiCall(@Query("visit-id") int visitId);

        @GET("search-text-globally")
        CompletableFuture<TextVisitPatientPageDTO> searchTextGlobally(@Query("text") String text,
                                                                      @Query("page") int page);

        @GET("list-todays-hotline")
        CompletableFuture<List<HotlineDTO>> listTodaysHotline();

        @GET("list-todays-hotline")
        Call<List<HotlineDTO>> listTodaysHotlineSync();

        @GET("list-recent-hotline")
        CompletableFuture<List<HotlineDTO>> listRecentHotline(@Query("threshold-hotline-id") int thresholdHotlineId);

        @GET("list-recent-hotline")
        Call<List<HotlineDTO>> listRecentHotlineSync(@Query("threshold-hotline-id") int thresholdHotlineId);

        @POST("enter-hotline")
        CompletableFuture<Integer> enterHotline(@Body HotlineDTO hotline);

        @GET("list-todays-hotline-in-range")
        CompletableFuture<List<HotlineDTO>> listTodaysHotlineInRange(@Query("after") int afterId,
                                                                     @Query("before") int beforeId);

        @GET("list-todays-hotline-in-range")
        Call<List<HotlineDTO>> listTodaysHotlineInRangeCall(@Query("after") int afterId,
                                                            @Query("before") int beforeId);

        @GET("get-powder-drug-config-file-path")
        CompletableFuture<StringResultDTO> getPowderDrugConfigFilePath();

        @GET("get-powder-drug-config-file-path")
        Call<StringResultDTO> getPowderDrugConfigFilePathCall();

        @GET("get-master-map-config-file-path")
        CompletableFuture<StringResultDTO> getMasterMapConfigFilePath();

        @GET("get-master-map-config-file-path")
        Call<StringResultDTO> getMasterMapConfigFilePathCall();

        @GET("get-shinryou-byoumei-map-config-file-path")
        CompletableFuture<StringResultDTO> getShinryouByoumeiMapConfigFilePath();

        @GET("get-shinryou-byoumei-map-config-file-path")
        Call<StringResultDTO> getShinryouByoumeiMapConfigFilePathCall();

        @GET("get-name-map-config-file-path")
        CompletableFuture<StringResultDTO> getNameMapConfigFilePath();

        @GET("get-name-map-config-file-path")
        Call<StringResultDTO> getNameMapConfigFilePathCall();

        @POST("batch-resolve-shinryou-names")
        CompletableFuture<Map<String, Integer>> batchResolveShinryouNames(
                @Query("at") LocalDate at, @Body List<List<String>> args);

        @POST("batch-resolve-kizai-names")
        CompletableFuture<Map<String, Integer>> batchResolveKizaiNames(
                @Query("at") LocalDate at, @Body List<List<String>> args);

        @POST("batch-resolve-byoumei-names")
        CompletableFuture<Map<String, Integer>> batchResolveByoumeiNames(
                @Query("at") LocalDate at, @Body List<List<String>> args);

        @POST("batch-resolve-shuushokugo-names")
        CompletableFuture<Map<String, Integer>> batchResolveShuushokugoNames(
                @Query("at") LocalDate at, @Body List<List<String>> args);

        @GET("list-visit-id-in-date-range")
        CompletableFuture<List<Integer>> listVisitIdInDateRange(
                @Query("from") LocalDate from, @Query("upto") LocalDate upto
        );
    }

    public static ServerAPI api;
    public static OkHttpClient client;
    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

    static {
        logging.setLevel(HttpLoggingInterceptor.Level.NONE);
    }

    static public void setServerUrl(String serverUrl) {
        if (!serverUrl.endsWith("/")) {
            serverUrl += "/";
        }
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        client = httpClient.build();

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        //module.addDeserializer(Op.class, new JacksonOpDeserializer());
        mapper.registerModule(module);
        Retrofit server = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                //.addCallAdapterFactory(Java8CallAdapterFactory.create())
                .client(client)
                .build();
        api = server.create(ServerAPI.class);
    }

    public static void setLogBody() {
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    public static void stop() {
        OkHttpClient client = dev.myclinic.vertx.client.Service.client;
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
        Cache cache = client.cache();
        if (cache != null) {
            try {
                cache.close();
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
        }
    }

}