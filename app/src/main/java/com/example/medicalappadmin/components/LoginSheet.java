package com.example.medicalappadmin.components;

import static com.example.medicalappadmin.Tools.Methods.hideKeyboard;

import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.medicalappadmin.Models.LinkedPatient;
import com.example.medicalappadmin.Models.Page;
import com.example.medicalappadmin.CaseDetailsActivity;
import com.example.medicalappadmin.R;
import com.example.medicalappadmin.Tools.Methods;
import com.example.medicalappadmin.adapters.PagesHistoryAdapter;
import com.example.medicalappadmin.adapters.RelativePreviousCasesAdapter;
import com.example.medicalappadmin.databinding.BsheetAddMobileNoBinding;
import com.example.medicalappadmin.databinding.DialogViewCaseBinding;
import com.example.medicalappadmin.rest.api.APIMethods;
import com.example.medicalappadmin.rest.api.interfaces.APIResponseListener;
import com.example.medicalappadmin.rest.requests.AddDetailsReq;
import com.example.medicalappadmin.rest.requests.AddMobileNoReq;
import com.example.medicalappadmin.rest.requests.LinkPageReq;
import com.example.medicalappadmin.rest.response.AddDetailsRP;
import com.example.medicalappadmin.rest.response.AddMobileNoRP;
import com.example.medicalappadmin.rest.response.InitialisePageRP;
import com.example.medicalappadmin.rest.response.LinkPageRP;
import com.example.medicalappadmin.rest.response.ViewCaseRP;
import com.example.medicalappadmin.rest.response.ViewPatientRP;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Objects;

public class LoginSheet {
    private static LoginSheet instance;
    BottomSheetDialog dialog;
    BsheetAddMobileNoBinding binding;
    AppCompatActivity context;
    private int pageNo;
    private String gender = "M";
    private PatientDetailsListener listener;
    private InitialisePageRP currentPageRP;

    private LoginSheet(AppCompatActivity context, int pageNo, InitialisePageRP currentPageRP) {
        this.context = context;
        this.pageNo = pageNo;
        this.currentPageRP = currentPageRP;
        if (context instanceof PatientDetailsListener) {
            listener = (PatientDetailsListener) context;
        }
        dialog = new BottomSheetDialog(context);
        binding = BsheetAddMobileNoBinding.inflate(context.getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        binding.btnBSAMNNext.setOnClickListener(view -> {
            if (binding.etBSMobile.getText().length() < 10) {
                binding.bsAMNActionText.setVisibility(View.VISIBLE);
                binding.bsAMNActionText.setText("Enter a valid mobile number");
            } else {
                binding.bsAMNActionText.setVisibility(View.VISIBLE);
                binding.bsAMNActionText.setText("Linking mobile. Please wait...");
                binding.bsAMNActionText.setTextColor(context.getColor(R.color.colorCta));
                linkMobileNumber();
            }
        });
        handleGender();
    }

    public static LoginSheet getInstance(AppCompatActivity context, int pageNo, InitialisePageRP currentPageRP) {
        if (instance == null || instance.pageNo != pageNo || instance.context != context) {
            instance = new LoginSheet(context, pageNo, currentPageRP);
        }
        return instance;
    }

    public void inputCharacter(int character) {
        if (!dialog.isShowing()) {
            dialog.show();
        }
        if (currentPageRP.getPatient() != null) return;
        if (String.valueOf(currentPageRP.getPage().getMobileNumber()).length() >= 10) return;
        if (character == 10){
            if (binding.etBSMobile.getText().length() == 0) {
                Toast.makeText(context, "All Values cleared", Toast.LENGTH_SHORT).show();
                return;
            };
            String text = binding.etBSMobile.getText().toString();
            text = text.substring(0, text.length()-1);
            binding.etBSMobile.setText(text);
            return;
        }
        binding.etBSMobile.setText(binding.etBSMobile.getText() + String.valueOf(character));
        if (binding.etBSMobile.getText().length() >= 10) {
            linkMobileNumber();
        }
    }

    private void linkMobileNumber() {
        AddMobileNoReq req = new AddMobileNoReq(Long.parseLong(binding.etBSMobile.getText().toString()), pageNo);

        APIMethods.addMobileNumber(context, req, new APIResponseListener<AddMobileNoRP>() {
            @Override
            public void success(AddMobileNoRP response) {
                if (listener != null) listener.phoneNoAdded(response, pageNo);
                handleAddMobileResponse(response);
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                Methods.showError(context,message,cancellable);
            }
        });
    }

    public void handleAddMobileResponse(AddMobileNoRP response) {

        if (response.getPatients().size() != 0) {
            //relatives exist
            showPreviousPatientsLayout();
            ArrayList<LinkedPatient> relatives = response.getPatients();
            binding.relativeRadioSelector.clearCheck();
            binding.relativeRadioSelector.removeAllViews();

            for (int i = 0; i < response.getPatients().size(); i++) {
                RadioButton button = new RadioButton(context);
                button.setText(relatives.get(i).getFullName());
                button.setTextSize(16);
                button.setTextColor(context.getColor(R.color.colorPrimTxt));
                button.setId(i);
                binding.relativeRadioSelector.addView(button);
            }

            //RadioButton for new patient
            RadioButton button = new RadioButton(context);
            button.setText("Other");
            button.setTextSize(16);
            button.setTextColor(context.getColor(R.color.colorPrimTxt));
            button.setId(relatives.size());
            binding.relativeRadioSelector.addView(button);
            binding.btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideKeyboard(view);
                    int selectedRelativeId = binding.relativeRadioSelector.getCheckedRadioButtonId();
                    if (selectedRelativeId != -1) {
                        showRelativeDetails(selectedRelativeId, relatives);
                    } else {
                        Toast.makeText(context, "Select a relative first", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            //new patient
            showAddNewPatientLayout();
            setBtnSaveListener();
        }
    }

    public void showRelativeDetails(int selectedRelativeId, ArrayList<LinkedPatient> relatives) {
        if (selectedRelativeId == relatives.size()) {
            showAddNewPatientLayout();
            setBtnSaveListener();
        } else if (selectedRelativeId < relatives.size()) {
            LinkedPatient selectedRelative = relatives.get(selectedRelativeId);
            showRelativePrevCases(selectedRelative);
        } else {
            Log.i("Adi", "showRelativeDetails: relatives null");
        }
    }

    private void setBtnSaveListener() {
        binding.btnBSSave.setOnClickListener(view -> {
            AddDetailsReq req = new AddDetailsReq();
            req.setPageNumber(pageNo);
            req.setMobileNumber(Long.parseLong(binding.etBSMobile.getText().toString()));
            req.setGender(gender);
            if (binding.etBSFullName.getText() != null && !binding.etBSFullName.getText().toString().isEmpty())
                req.setFullName(binding.etBSFullName.getText().toString());
            if (binding.etBSEmail.getText() != null && !binding.etBSEmail.getText().toString().isEmpty())
                req.setEmail(binding.etBSEmail.getText().toString());
            binding.pbBSSaveNewPatient.setVisibility(View.VISIBLE);
            APIMethods.addDetails(context, req, new APIResponseListener<AddDetailsRP>() {
                @Override
                public void success(AddDetailsRP response) {
                    binding.pbBSSaveNewPatient.setVisibility(View.GONE);
                    if (listener != null) listener.detailsAdded(response, pageNo);
                    showPatientDetails(response);
                }

                @Override
                public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                    binding.pbBSSaveNewPatient.setVisibility(View.GONE);
                    Methods.showError(context,message,cancellable);
                }
            });
        });
    }

    public void showPatientDetails(AddDetailsRP response) {
        binding.llBSPrevPatientList.setVisibility(View.GONE);
        binding.llBSAddMobileNumber.setVisibility(View.GONE);
        binding.llBSNewPatient.setVisibility(View.GONE);
        binding.llBSExistingPatientDetails.setVisibility(View.VISIBLE);
        binding.llBSRelPrevCases.setVisibility(View.GONE);

        if (response.getFullName() != null)
            binding.tvBSEPatientName.setText(response.getFullName());
        binding.tvBSEPatientsNo.setText(String.valueOf(response.getMobileNumber()));
        if (response.getGender().equals("M"))
            binding.tvBSEPGender.setText("Male");
        else if (response.getGender().equals("F")) {
            binding.tvBSEPGender.setText("Female");
        }
    }

    public void showAddNewPatientLayout() {
        binding.llBSPrevPatientList.setVisibility(View.GONE);
        binding.llBSAddMobileNumber.setVisibility(View.GONE);
        binding.llBSNewPatient.setVisibility(View.VISIBLE);
        binding.llBSExistingPatientDetails.setVisibility(View.GONE);
        binding.llBSRelPrevCases.setVisibility(View.GONE);
        setBtnSaveListener();

    }

    public void showRelativePrevCases(LinkedPatient selectedRelative) {
        binding.pbSelectRelative.setVisibility(View.VISIBLE);
        String relativeId = selectedRelative.get_id();
        APIMethods.viewPatient(context, relativeId, new APIResponseListener<ViewPatientRP>() {
            @Override
            public void success(ViewPatientRP response) {
                binding.pbSelectRelative.setVisibility(View.GONE);

                if (listener != null)
                    listener.relativePreviousCases(response, selectedRelative, relativeId, pageNo);
                showRelativesCasesLayout(response, relativeId, selectedRelative);
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
//                pbSelectRelative.setVisibility(View.GONE);
//                Log.i(TAG, "fail: rel prev case");
//                showError(message, null);
                binding.pbSelectRelative.setVisibility(View.GONE);
                Methods.showError(context,message,cancellable);
            }
        });


    }

    public void showRelativesCasesLayout(ViewPatientRP response, String relativeId, LinkedPatient selectedRelative) {
        binding.llBSPrevPatientList.setVisibility(View.GONE);
        binding.llBSAddMobileNumber.setVisibility(View.GONE);
        binding.llBSNewPatient.setVisibility(View.GONE);
        binding.llBSExistingPatientDetails.setVisibility(View.GONE);
        binding.llBSRelPrevCases.setVisibility(View.VISIBLE);
        binding.rcvBSRelPrevCases.setLayoutManager(new LinearLayoutManager(context));
        RelativePreviousCasesAdapter adapter = new RelativePreviousCasesAdapter(response, context, new RelativePreviousCasesAdapter.SelectCaseListener() {
            @Override
            public void onCaseSelected(String caseId) {
                linkPageToCase(caseId, relativeId, selectedRelative);
            }

            @Override
            public void onViewRelCaseClicked(String caseId) {
                showCaseDetailsDialog(caseId);
            }
        });
        binding.rcvBSRelPrevCases.setAdapter(adapter);
        binding.btnBSRelNewCase.setOnClickListener(view -> {
            linkPageToPatient(selectedRelative);
        });
        binding.pbSelectRelative.setVisibility(View.GONE);
    }

    private void showCaseDetailsDialog(String caseId) {

        Dialog viewCaseDialog = new Dialog(context);
        DialogViewCaseBinding dialogViewCaseBinding = DialogViewCaseBinding.inflate(context.getLayoutInflater());
        viewCaseDialog.setContentView(dialogViewCaseBinding.getRoot());
        Window window = viewCaseDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(layoutParams);
        }
        viewCaseDialog.show();

        dialogViewCaseBinding.ivCloseDialog.setOnClickListener(view -> viewCaseDialog.dismiss());
        dialogViewCaseBinding.tvOpenCaseInDetail.setOnClickListener(view -> {
            Intent intent = new Intent(context, CaseDetailsActivity.class);
            intent.putExtra("CASE_ID",caseId);
            context.startActivity(intent);
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,2);
        APIMethods.viewCase(context, caseId, new APIResponseListener<ViewCaseRP>() {
            @Override
            public void success(ViewCaseRP response) {
                dialogViewCaseBinding.pbRelViewCase.setVisibility(View.GONE);
                dialogViewCaseBinding.llCaseData.setVisibility(View.VISIBLE);
                dialogViewCaseBinding.tvOpenCaseInDetail.setVisibility(View.VISIBLE);
                dialogViewCaseBinding.tvOpenCaseInDetail.setEnabled(true);
                dialogViewCaseBinding.tvRelCaseName.setText(response.getTitle());
                dialogViewCaseBinding.tvRelLastUpdated.setText(response.getUpdatedAt());

                dialogViewCaseBinding.rcvRelCasePages.setLayoutManager(gridLayoutManager);
                dialogViewCaseBinding.rcvRelCasePages.setAdapter(new PagesHistoryAdapter(response, context, new PagesHistoryAdapter.PageListener() {
                    @Override
                    public void onPageClicked(ArrayList<Page> pages, int currentposition) {
                        Log.i("Pres login sheet", "onPageClicked: rel case" + currentposition);
                    }
                }));
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                viewCaseDialog.dismiss();
                Toast.makeText(context, "Error encountered. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void linkPageToPatient(LinkedPatient selectedRelative) {
        LinkPageReq req = new LinkPageReq();
        if (selectedRelative == null) return;
        req.setPatientId(selectedRelative.get_id());
        req.setPageNumber(pageNo);
        linkPatientApi(req, selectedRelative);

    }

    public void linkPageToCase(String caseId, String relativeId, LinkedPatient selectedRelative) {
        LinkPageReq req = new LinkPageReq();
        req.setPageNumber(pageNo);
        req.setPatientId(relativeId);
        req.setCaseId(caseId);

        linkPatientApi(req, selectedRelative);
    }

    public void linkPatientApi(LinkPageReq req, LinkedPatient selectedRelative) {
        binding.pbSelectRelative.setVisibility(View.VISIBLE);

        APIMethods.linkPage(context, req, new APIResponseListener<LinkPageRP>() {
            @Override
            public void success(LinkPageRP response) {
                Toast.makeText(context, "Page is linked to " + selectedRelative.getFullName(), Toast.LENGTH_SHORT).show();
                binding.pbSelectRelative.setVisibility(View.GONE);
                showExistingPatientLayout(selectedRelative.getFullName(), selectedRelative.getGender(), String.valueOf(selectedRelative.getMobileNumber()));

            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
//                showError(message, null);
                Methods.showError(context,message,cancellable);
                binding.pbSelectRelative.setVisibility(View.GONE);

            }
        });

    }

    public void showExistingPatientLayout(String name, String gender, String mobileNo) {
        binding.llBSPrevPatientList.setVisibility(View.GONE);
        binding.llBSAddMobileNumber.setVisibility(View.GONE);
        binding.llBSNewPatient.setVisibility(View.GONE);
        binding.llBSExistingPatientDetails.setVisibility(View.VISIBLE);
        binding.llBSRelPrevCases.setVisibility(View.GONE);
        binding.tvBSEPatientName.setText(name);
        if (Objects.equals(gender, "M")) {
            binding.tvBSEPGender.setText("Male");
        } else {
            binding.tvBSEPGender.setText("Female");
        }
        binding.tvBSEPatientsNo.setText(String.valueOf(mobileNo));
    }

    private void showPreviousPatientsLayout() {
        binding.llBSPrevPatientList.setVisibility(View.VISIBLE);
        binding.llBSAddMobileNumber.setVisibility(View.GONE);
        binding.llBSNewPatient.setVisibility(View.GONE);
        binding.llBSExistingPatientDetails.setVisibility(View.GONE);
        binding.llBSRelPrevCases.setVisibility(View.GONE);

    }

    private void handleGender() {
        binding.tvBSMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender = "M";
                binding.tvBSMale.setBackgroundColor(context.getColor(R.color.blue_bg));
                binding.tvBSFemale.setBackgroundColor(context.getColor(R.color.colorBackground));
                binding.tvBSMale.setTextColor(context.getColor(R.color.white));
                binding.tvBSFemale.setTextColor(context.getColor(R.color.colorPrimTxt));
            }
        });
        binding.tvBSFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender = "F";
                binding.tvBSFemale.setBackgroundColor(context.getColor(R.color.blue_bg));
                binding.tvBSMale.setBackgroundColor(context.getColor(R.color.colorBackground));
                binding.tvBSFemale.setTextColor(context.getColor(R.color.white));
                binding.tvBSMale.setTextColor(context.getColor(R.color.colorPrimTxt));
            }
        });
    }

    public interface PatientDetailsListener {
        void phoneNoAdded(AddMobileNoRP response, int pageNo);

        void relativePreviousCases(ViewPatientRP response, LinkedPatient selectedRelative, String relativeId, int pageNo);

        void detailsAdded(AddDetailsRP response, int pageNo);
    }

    public static void hideSheet(){
        if (instance != null && instance.dialog != null && instance.dialog.isShowing()){
            instance.dialog.dismiss();
        }
    }


}
