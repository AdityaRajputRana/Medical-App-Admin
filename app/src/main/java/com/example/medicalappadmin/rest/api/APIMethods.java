package com.example.medicalappadmin.rest.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.medicalappadmin.Models.FileMetadata;
import com.example.medicalappadmin.Models.Page;
import com.example.medicalappadmin.Models.Patient;
import com.example.medicalappadmin.Models.Point;
import com.example.medicalappadmin.Models.User;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.api.interfaces.FileTransferResponseListener;
import com.example.medicalappadmin.rest.requests.AddDetailsReq;
import com.example.medicalappadmin.rest.requests.AddGuideVideoReq;
import com.example.medicalappadmin.rest.requests.AddMobileNoReq;
import com.example.medicalappadmin.rest.requests.CaseHistoryReq;
import com.example.medicalappadmin.rest.requests.CaseSubmitReq;
import com.example.medicalappadmin.rest.requests.EmptyReq;
import com.example.medicalappadmin.rest.requests.InitialisePageReq;
import com.example.medicalappadmin.rest.requests.LinkGuideReq;
import com.example.medicalappadmin.rest.requests.LinkPageReq;
import com.example.medicalappadmin.rest.requests.LoginReq;
import com.example.medicalappadmin.rest.requests.MergeCasesReq;
import com.example.medicalappadmin.rest.requests.PatientListReq;
import com.example.medicalappadmin.rest.requests.SetGuidePosReq;
import com.example.medicalappadmin.rest.requests.SignupReq;
import com.example.medicalappadmin.rest.requests.UploadPointsReq;
import com.example.medicalappadmin.rest.requests.UploadVoiceReq;
import com.example.medicalappadmin.rest.requests.ViewCaseReq;
import com.example.medicalappadmin.rest.requests.ViewPatientReq;
import com.example.medicalappadmin.rest.response.AddDetailsRP;
import com.example.medicalappadmin.rest.response.AddGuideVideoRP;
import com.example.medicalappadmin.rest.response.AddMobileNoRP;
import com.example.medicalappadmin.rest.response.CaseHistoryRP;
import com.example.medicalappadmin.rest.response.GeneratePDFLinkRP;
import com.example.medicalappadmin.rest.response.ConfigurePageRP;
import com.example.medicalappadmin.rest.response.EmptyRP;
import com.example.medicalappadmin.rest.response.GuidesVideosRP;
import com.example.medicalappadmin.rest.response.HomePageRP;
import com.example.medicalappadmin.rest.response.InitialisePageRP;
import com.example.medicalappadmin.rest.response.LinkGuideRP;
import com.example.medicalappadmin.rest.response.LinkPageRP;
import com.example.medicalappadmin.rest.response.LoginRP;
import com.example.medicalappadmin.rest.response.PatientListRP;
import com.example.medicalappadmin.rest.response.SetGuidePosRP;
import com.example.medicalappadmin.rest.response.SignupRP;
import com.example.medicalappadmin.rest.response.SubmitCaseRP;
import com.example.medicalappadmin.rest.response.UploadVoiceRP;
import com.example.medicalappadmin.rest.response.ViewCaseRP;
import com.example.medicalappadmin.rest.response.ViewPatientRP;

import java.io.File;
import java.util.ArrayList;


public class APIMethods {

    public static void getPage(Context context, int pageNumber, APIResponseListener<Page> listener){
        InitialisePageReq req = new InitialisePageReq(pageNumber);
        API.postData(listener, req, EndPoints.getPage, Page.class, context);
    }

    public static void homePage(Context context, APIResponseListener<HomePageRP> listener){
        API.postData(listener,new EmptyReq(), EndPoints.homePage, HomePageRP.class, context);
    }
    public static void configurePage(Context context, EmptyReq req, APIResponseListener<ConfigurePageRP> listener){
        API.postData(listener,req, EndPoints.configurePage, ConfigurePageRP.class, context, true);
    }

    public static void configurePageForceCache(Context context, APIResponseListener<ConfigurePageRP> listener){
        API.postData(listener,new EmptyReq(), EndPoints.configurePage, ConfigurePageRP.class, context, true, true);
    }
    public static void linkAdditionalGuide(Context context, LinkGuideReq req, APIResponseListener<LinkGuideRP> listener){
        API.postData(listener,req, EndPoints.linkAdditionalGuide, LinkGuideRP.class, context);
    }
    public static void uploadVoice(Context context, File file, int pageNumber, FileTransferResponseListener<UploadVoiceRP> listener){
        UploadVoiceReq req = new UploadVoiceReq(new FileMetadata("mp3", "audio/mp3", "Voice"), pageNumber);
        API.postFile(context, file, req, EndPoints.uploadAdditional, UploadVoiceRP.class, listener, "test", "mp3");
    }

    public static void uploadAttachment(Context context, File file, int pageNumber, FileMetadata metadata,
                                        FileTransferResponseListener<UploadVoiceRP> listener){
        UploadVoiceReq req = new UploadVoiceReq(metadata, pageNumber);
        API.postFile(context, file, req, EndPoints.uploadAdditional, UploadVoiceRP.class, listener, metadata.fileName, metadata.ext);
    }


    //login method
    public static void loginWithEmailAndPassword(Context context, String email, String password, APIResponseListener<LoginRP> listener) {
        LoginReq req = new LoginReq(email, password);
        API.postData(listener, req, EndPoints.login, LoginRP.class, context);
    }

    //signup method for staff
    public static void signUpStaff(Context context, User user, APIResponseListener<SignupRP> listener) {
        SignupReq signupReq = new SignupReq(user);
        API.postData(listener, signupReq, EndPoints.signupStaff, SignupRP.class, context);
    }


    public static void initialisePage(Context context, int pageNumber, APIResponseListener<InitialisePageRP> listener) {
        InitialisePageReq req = new InitialisePageReq(pageNumber);
        API.postData(listener, req, EndPoints.initialisePage, InitialisePageRP.class, context);
    }


    public static void uploadPoints(Context context, int pageNumber, ArrayList<Point> points, APIResponseListener<EmptyRP> listener) {
        UploadPointsReq req = new UploadPointsReq(pageNumber, points);
        API.postData(listener, req, EndPoints.uploadPoints, EmptyRP.class, context);

    }

    //Upload details

    public static void addDetails(Context context, AddDetailsReq req, APIResponseListener<AddDetailsRP> listener) {
        API.postData(listener, req, EndPoints.addDetails, AddDetailsRP.class, context);
    }


    //Load cases history
    public static void loadCaseHistory(Context context, int pageNumber, APIResponseListener<CaseHistoryRP> listener) {
        CaseHistoryReq req = new CaseHistoryReq(pageNumber);
        API.postData(listener, req, EndPoints.caseHistory, CaseHistoryRP.class, context);
    }


    //merge cases
    public static void mergeCasesHistory(Context context, String fromCaseId, String toCaseId, APIResponseListener<EmptyRP> listener) {
        MergeCasesReq req = new MergeCasesReq(fromCaseId, toCaseId);
        API.postData(listener, req, EndPoints.mergeCases, EmptyRP.class, context);
    }

    //submit case

    public static void submitCase(Context context, String caseId, APIResponseListener<SubmitCaseRP> listener) {
        CaseSubmitReq req = new CaseSubmitReq(caseId);
        API.postData(listener, req, EndPoints.submitCaseToPatient, SubmitCaseRP.class, context);
    }

    public static void generatePDFonServer(Context context, String caseId, APIResponseListener<GeneratePDFLinkRP> listener) {
        CaseSubmitReq req = new CaseSubmitReq(caseId);
        API.postData(listener, req, EndPoints.generateCasePDF, GeneratePDFLinkRP.class, context);
    }

    //view case
    public static void viewCase(Context context, String caseId, APIResponseListener<ViewCaseRP> listener) {
        ViewCaseReq req = new ViewCaseReq(caseId);
        API.postData(listener, req, EndPoints.viewCase, ViewCaseRP.class, context);

    }

    //link mobile no
    public static void addMobileNumber(Context context, AddMobileNoReq req, APIResponseListener<AddMobileNoRP> listener) {
        API.postData(listener, req, EndPoints.addMobileNo, AddMobileNoRP.class, context);
    }

    public  static void linkPage(Context context, LinkPageReq req,APIResponseListener<LinkPageRP> listener){
        API.postData(listener,req,EndPoints.linkPage, LinkPageRP.class,context);
    }

    public static void loadPatientsList(Context context, int pageNumber, APIResponseListener<PatientListRP> listener) {
        PatientListReq req = new PatientListReq(pageNumber);
        API.postData(listener, req, EndPoints.patientList, PatientListRP.class, context);
    }

    public static void loadPatientsList(Context context, int pageNumber, String searchQuery,  APIResponseListener<PatientListRP> listener) {
        PatientListReq req = new PatientListReq(pageNumber, searchQuery);
        API.postData(listener, req, EndPoints.patientList, PatientListRP.class, context);
    }



    public static void viewPatient(Context context, String patientId, APIResponseListener<ViewPatientRP> listener) {
         ViewPatientReq req = new ViewPatientReq(patientId);
        API.postData(listener, req, EndPoints.viewPatient, ViewPatientRP.class, context);

    }
    public static void listGuidesVideos(Context context, APIResponseListener<GuidesVideosRP> listener) {
        EmptyReq req = new EmptyReq();
        API.postData(listener, req, EndPoints.viewGuideVideos, GuidesVideosRP.class, context);
    }

    public static void addGuideVideo(Context context, AddGuideVideoReq req, APIResponseListener<AddGuideVideoRP> listener) {
        API.postData(listener, req, EndPoints.addGuideVideo, AddGuideVideoRP.class, context);
    }
    public static void setGuideVideoPosition(Context context, SetGuidePosReq req, APIResponseListener<SetGuidePosRP> listener) {
        API.postData(listener, req, EndPoints.setGuidePosition, SetGuidePosRP.class, context);
    }

    public static void addNewPatient(Context context, Patient patient, APIResponseListener<ViewPatientRP> listener){
        API.postData(listener, patient, EndPoints.addNewPatient, ViewPatientRP.class, context);
    }





}