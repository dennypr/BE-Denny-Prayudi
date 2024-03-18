package com.bdi.controller.custody;

import com.bdi.common.HttpUtils;
import com.bdi.model.*;
import com.bdi.model.custody.*;
import com.bdi.model.orr.audittrail.*;
import com.bdi.model.custody.laporankoreksi.template.*;
import com.bdi.model.orr.pelaporan.crs.CRSORRGetFinalDataAccountReport;
import com.bdi.model.orr.pelaporan.domestik.DJPDomestikModel;
import com.bdi.model.orr.settings.RoleModel;
import com.bdi.model.orr.settings.UserRoleModel;
import com.bdi.model.custody.laporankoreksi.djp.*;
import com.bdi.repository.UserAccessRepository;
import com.bdi.repository.custody.crscustodyimport.CRSCustodyResult_TTRepository;
import com.bdi.repository.custody.crscustodyimport.djp.*;
import com.bdi.repository.custody.crscustodyimport.template.*;
import com.bdi.repository.custody.domestikimport.ImportCustodyUploadDataRepository;
import com.bdi.repository.custody.domestikimport.template.*;
import com.bdi.repository.custody.domestikimport.CustodyResult_TTRepository;
import com.bdi.repository.custody.domestikimport.djp.ImportCustodyKoreksiDjpRepository;
import com.bdi.repository.custody.domestikimport.djp.ImportCustodyPengendaliEntitasKoreksiDjpRepository;
import com.bdi.repository.orr.UserRoleRepository;
import com.bdi.service.custody.AuditTrailCustodyService;
import com.bdi.service.custody.CustodyApprovalService;
import com.bdi.service.custody.CustodyPelaporanService;
import com.bdi.service.orr.PreValidationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/custody/")
public class CustodyApprovalController {


    @Autowired
    private CustodyPelaporanService custodyPelaporanService;

    @Autowired
    private AuditTrailCustodyService auditTrailCustodyService;

    @Autowired
    private ApprovalCustodyRepository approvalCustodyRepository;

    @Autowired
    private UserRoleRepository UserRoleRepository;

    @Autowired
    private ImportCustodyRepository importCustodyRepository;

    @Autowired
    private CustodyResult_TTRepository custodyResult_TTRepository;

    @Autowired
    private ImportCustodyKoreksiDjpRepository importCustodyKoreksiDjpRepository;

    @Autowired
    private ImportCustodyPengendaliEntitasRepository importCustodyPengendaliEntitasRepository;

    @Autowired
    private ImportCustodyPengendaliEntitasKoreksiDjpRepository importCustodyPengendaliEntitasKoreksiDjpRepository;

    @Autowired
    private ImportCustodyAccountReportRepository importCustodyAccountReportRepository;


    @Autowired
    private ImportCustodyTinIndividualRepository importCustodyTinIndividualRepository;

    @Autowired
    private ImportCustodyInOrganizationRepository importCustodyInOrganizationRepository;

    @Autowired
    private ImportCustodyNameIndividualRepository importCustodyNameIndividualRepository;

    @Autowired
    private ImportCustodyNameOrganizationRepository importCustodyNameOrganizationRepository;

    @Autowired
    private ImportCustodyRepAddressRepository importCustodyRepAddressRepository;

    @Autowired
    private ImportCustodyRepPaymentRepository importCustodyRepPaymentRepository;

    @Autowired
    private ImportCustodyAccountReportDjpRepository importCustodyAccountReportDjpRepository;

    @Autowired
    private ImportCustodyInOrganizationDjpRepository importCustodyInOrganizationDjpRepository;

    @Autowired
    private ImportCustodyNameIndividualDjpRepository importCustodyNameIndividualDjpRepository;

    @Autowired
    private ImportCustodyNameOrganizationDjpRepository importCustodyNameOrganizationDjpRepository;

    @Autowired
    private ImportCustodyRepAddressDjpRepository importCustodyRepAddressDjpRepository;

    @Autowired
    private ImportCustodyRepPaymentDjpRepository importCustodyRepPaymentDjpRepository;

    @Autowired
    private ImportCustodyTinIndividualDjpRepository importCustodyTinIndividualDjpRepository;

    @Autowired
    private CRSCustodyResult_TTRepository cRSCustodyResult_TTRepository;

    @Autowired
    private ImportCustodyDeleteRepository importCustodyDeleteRepository;

    @Autowired
    private ImportCustodyPengendaliEntitasDeleteRepository importCustodyPengendaliEntitasDeleteRepository;

    @Autowired
    private UserAccessRepository userAccessRepository;

    @Autowired
    private ImportCustodyUploadDataRepository importCustodyUploadDataRepository;


    @Autowired
    private ImportDeleteCustodyAccountReportRepository importDeleteCustodyAccountReportRepository;
    @Autowired
    private ImportDeleteCustodyTinIndividualRepository importDeleteCustodyTinIndividualRepository;

    @Autowired
    private ImportDeleteCustodyInOrganizationRepository importDeleteCustodyInOrganizationRepository;

    @Autowired
    private ImportDeleteCustodyNameIndividualRepository importDeleteCustodyNameIndividualRepository;

    @Autowired
    private ImportDeleteCustodyNameOrganizationRepository importDeleteCustodyNameOrganizationRepository;

    @Autowired
    private ImportDeleteCustodyRepAddressRepository importDeleteCustodyRepAddressRepository;

    @Autowired
    private ImportDeleteCustodyRepPaymentRepository importDeleteCustodyRepPaymentRepository;

    @Autowired
    private CustodyApprovalService custodyApprovalService;

    private ObjectMapper objectMapper = new ObjectMapper();

    DecimalFormat twoPlaces = new DecimalFormat("0.00");

    DecimalFormat noPlaces = new DecimalFormat("0");

    @GetMapping("approval")
    public String getApproval(RoleModel pRoleModel, HttpServletRequest pRequest, Model pModel)
            throws IOException, JAXBException, ParseException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());

        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "View";
        String AccessScreen     = "View Approval Edit Data Domestik Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);

        if (cookies != null) {
            log.info("Cookies List : " + Arrays.stream(cookies)
                    .map(c -> c.getName() + "=" + c.getValue()).collect(Collectors.joining(", ")));

            //get personal
            List<DJPImportCustodyModel> dJPImportCustodyModel = new ArrayList<>();
            dJPImportCustodyModel = approvalCustodyRepository.getAllUploadApproval();

            if (dJPImportCustodyModel.isEmpty()) {
                pModel.addAttribute("alreadyDataPersonal", false);
            } else {
                pModel.addAttribute("alreadyDataPersonal", true);
                pModel.addAttribute("totalRoleDataPersonal", dJPImportCustodyModel.size());
                pModel.addAttribute("userRoleDataPersonal", dJPImportCustodyModel);
            }

            //get non personal
            List<DJPImportCustodyPengendaliEntitasModel> dJPImportPengendaliEntitasModel = new ArrayList<>();
            dJPImportPengendaliEntitasModel = importCustodyPengendaliEntitasRepository.getAllUploadApproval();

            if (dJPImportPengendaliEntitasModel.isEmpty()) {
                pModel.addAttribute("alreadyDataEntitas", false);
            } else {
                pModel.addAttribute("alreadyDataEntitas", true);
                pModel.addAttribute("totalRoleDataEntitas", dJPImportPengendaliEntitasModel.size());
                pModel.addAttribute("userRoleDataEntitas", dJPImportPengendaliEntitasModel);
            }


            //get hapus personal
            List<DJPImportDeleteCustodyModel> dJPImportDeleteCustodyModel = new ArrayList<>();
            dJPImportDeleteCustodyModel = importCustodyDeleteRepository.getAllUpload();

            if (dJPImportDeleteCustodyModel.isEmpty()) {
                pModel.addAttribute("alreadyData", false);
            } else {
                pModel.addAttribute("alreadyDataDelete", true);
                pModel.addAttribute("totalRoleDataDelete", dJPImportDeleteCustodyModel.size());
                pModel.addAttribute("userRoleDataDelete", dJPImportDeleteCustodyModel);
            }

            //get hapus non personal
            List<DJPImportDeleteCustodyPengendaliEntitasModel> dJPImportPengendaliEntitasDeleteModel = new ArrayList<>();
            dJPImportPengendaliEntitasDeleteModel = importCustodyPengendaliEntitasDeleteRepository.getAllUploadApproval();

            if (dJPImportPengendaliEntitasDeleteModel.isEmpty()) {
                pModel.addAttribute("alreadyDataEntitasDelete", false);
            } else {
                pModel.addAttribute("alreadyDataEntitasDelete", true);
                pModel.addAttribute("totalRoleDataEntitasDelete", dJPImportPengendaliEntitasDeleteModel.size());
                pModel.addAttribute("userRoleDataEntitasDelete", dJPImportPengendaliEntitasDeleteModel);
            }

            pModel.addAttribute("approvalReject", new ApproveReject());

            pModel.addAttribute("contentPath", "uploadapprovalorrcustody");
            pModel.addAttribute("contentName", "uploadapprovalorrcustody");
            UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
            pModel.addAttribute("var",userRole.getRoleId());
            List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
            pModel.addAttribute("roleUserAccess", userAccess);
            pModel.addAttribute("UserLogin",userRole.getName());
            pModel.addAttribute("User",userLoginApp);

            auditTrailCustodyService.postAuditTrail(userLoginApp,Operation,AccessScreen,"","");
            return "index";

        } else {
            log.info("Cookies Else");
            return "redirect:/login";
        }
    }

    @GetMapping("approval/data")
    public String getApprovalData(RoleModel pRoleModel, HttpServletRequest pRequest, Model pModel)
            throws IOException, JAXBException, ParseException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());

        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "View";
        String AccessScreen     = "View Apprval Upload Data Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);

        if (cookies != null) {
            log.info("Cookies List : " + Arrays.stream(cookies)
                    .map(c -> c.getName() + "=" + c.getValue()).collect(Collectors.joining(", ")));

            //get Upload Data

            List<DJPImportCustodyUploadDataModel> dJPImportCustodyModel = new ArrayList<>();
            dJPImportCustodyModel = importCustodyUploadDataRepository.findAllApproval();

            List<String> tahun = importCustodyUploadDataRepository.findYear();

            if (dJPImportCustodyModel.isEmpty()) {
                pModel.addAttribute("alreadyData", false);
            } else {
                pModel.addAttribute("alreadyData", true);
                pModel.addAttribute("totalRoleData", dJPImportCustodyModel.size());
                pModel.addAttribute("userRoleData", dJPImportCustodyModel);
                pModel.addAttribute("tahun", tahun);
            }


            pModel.addAttribute("contentPath", "uploadapprovaldatadomestikcustody");
            pModel.addAttribute("contentName", "uploadapprovaldatadomestikcustody");
            UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
            pModel.addAttribute("var",userRole.getRoleId());
            List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
            pModel.addAttribute("roleUserAccess", userAccess);
            pModel.addAttribute("UserLogin",userRole.getName());
            pModel.addAttribute("User",userLoginApp);

            auditTrailCustodyService.postAuditTrail(userLoginApp,Operation,AccessScreen,"","");
            return "index";

        } else {
            log.info("Cookies Else");
            return "redirect:/login";
        }
    }

    @GetMapping("approval/data/crs")
    public String getApprovalDataCrs(RoleModel pRoleModel, HttpServletRequest pRequest, Model pModel)
            throws IOException, JAXBException, ParseException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());

        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "View";
        String AccessScreen     = "View Apprval Upload Data CRS Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);

        if (cookies != null) {
            log.info("Cookies List : " + Arrays.stream(cookies)
                    .map(c -> c.getName() + "=" + c.getValue()).collect(Collectors.joining(", ")));

            //get Upload Data

            List<DJPImportCustodyUploadDataModel> dJPImportCustodyModel = new ArrayList<>();
            dJPImportCustodyModel = importCustodyUploadDataRepository.findAllApproval();

            List<String> tahun = importCustodyUploadDataRepository.findYear();

            if (dJPImportCustodyModel.isEmpty()) {
                pModel.addAttribute("alreadyData", false);
            } else {
                pModel.addAttribute("alreadyData", true);
                pModel.addAttribute("totalRoleData", dJPImportCustodyModel.size());
                pModel.addAttribute("userRoleData", dJPImportCustodyModel);
                pModel.addAttribute("tahun", tahun);
            }


            pModel.addAttribute("contentPath", "uploadapprovaldatacrscustody");
            pModel.addAttribute("contentName", "uploadapprovaldatacrscustody");
            UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
            pModel.addAttribute("var",userRole.getRoleId());
            List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
            pModel.addAttribute("roleUserAccess", userAccess);
            pModel.addAttribute("UserLogin",userRole.getName());
            pModel.addAttribute("User",userLoginApp);

            auditTrailCustodyService.postAuditTrail(userLoginApp,Operation,AccessScreen,"","");
            return "index";

        } else {
            log.info("Cookies Else");
            return "redirect:/login";
        }
    }

    @GetMapping("approval/crs")
    public String getApprovalCrs(RoleModel pRoleModel, HttpServletRequest pRequest, Model pModel)
            throws IOException, JAXBException, ParseException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());

        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "View";
        String AccessScreen     = "View Approval Edit Data CRS Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);


        if (cookies != null) {
            log.info("Cookies List : " + Arrays.stream(cookies)
                    .map(c -> c.getName() + "=" + c.getValue()).collect(Collectors.joining(", ")));

            //Account Report
            List<CRSImportCustodyModel> crsImportModel = new ArrayList<>();
            crsImportModel = importCustodyAccountReportRepository.getAllApproval();
            if (crsImportModel.isEmpty()) {
                pModel.addAttribute("alreadyData", false);
            } else {
                pModel.addAttribute("alreadyData", true);
                pModel.addAttribute("totalRoleData", crsImportModel.size());
                pModel.addAttribute("userRoleData", crsImportModel);
            }

            //TIN Individual
            List<CRSImportCustodyTinIndividualModel> tINIndividual = new ArrayList<>();
            tINIndividual = importCustodyTinIndividualRepository.getAllApproval();
            if (tINIndividual.isEmpty()) {
                pModel.addAttribute("alreadyDataTinIndividual", false);
            } else {
                pModel.addAttribute("alreadyDataTinIndividual", true);
                pModel.addAttribute("totalRoleDataTinIndividual", tINIndividual.size());
                pModel.addAttribute("userRoleDataTinIndividual", tINIndividual);
            }

            //In Organization
            List<CRSImportCustodyInOrganizationModel> inOrganization = new ArrayList<>();
            inOrganization = importCustodyInOrganizationRepository.getAllApproval();
            if (inOrganization.isEmpty()) {
                pModel.addAttribute("alreadyDatainOrganization", false);
            } else {
                pModel.addAttribute("alreadyDatainOrganization", true);
                pModel.addAttribute("totalRoleDatainOrganization", inOrganization.size());
                pModel.addAttribute("userRoleDatainOrganization", inOrganization);
            }

            //Name Individual
            List<CRSImportCustodyNameIndividualModel> nameIndividual = new ArrayList<>();
            nameIndividual = importCustodyNameIndividualRepository.getAllApproval();
            if (nameIndividual.isEmpty()) {
                pModel.addAttribute("alreadyDatanameIndividual", false);
            } else {
                pModel.addAttribute("alreadyDatanameIndividual", true);
                pModel.addAttribute("totalRoleDatanameIndividual", nameIndividual.size());
                pModel.addAttribute("userRoleDatanameIndividual", nameIndividual);
            }

            //Name Organization
            List<CRSImportCustodyNameOrganizationModel> nameOrganization = new ArrayList<>();
            nameOrganization = importCustodyNameOrganizationRepository.getAllApproval();
            if (nameOrganization.isEmpty()) {
                pModel.addAttribute("alreadyDatanameOrganization", false);
            } else {
                pModel.addAttribute("alreadyDatanameOrganization", true);
                pModel.addAttribute("totalRoleDatanameOrganization", nameOrganization.size());
                pModel.addAttribute("userRoleDatanameOrganization", nameOrganization);
            }

            //Rep Address
            List<CRSImportCustodyRepAddressModel> repAddress = new ArrayList<>();
            repAddress = importCustodyRepAddressRepository.getAllApproval();
            if (repAddress.isEmpty()) {
                pModel.addAttribute("alreadyDatarepAddress", false);
            } else {
                pModel.addAttribute("alreadyDatarepAddress", true);
                pModel.addAttribute("totalRoleDatarepAddress", repAddress.size());
                pModel.addAttribute("userRoleDatarepAddress", repAddress);
            }

            //Rep Payment
            List<CRSImportCustodyRepPaymentModel> repPayment = new ArrayList<>();
            repPayment = importCustodyRepPaymentRepository.getAllApproval();
            if (repPayment.isEmpty()) {
                pModel.addAttribute("alreadyDatarepPayment", false);
            } else {
                pModel.addAttribute("alreadyDatarepPayment", true);
                pModel.addAttribute("totalRoleDatarepPayment", repPayment.size());
                pModel.addAttribute("userRoleDatarepPayment", repPayment);
            }


            // Delete Account Report
            List<CRSImportDeleteCustodyModel> crsDeleteImportModel = new ArrayList<>();
            crsDeleteImportModel = importDeleteCustodyAccountReportRepository.getAllApproval();
            if (crsDeleteImportModel.isEmpty()) {
                pModel.addAttribute("alreadyDataDeleteAccountReport", false);
            } else {
                pModel.addAttribute("alreadyDataDeleteAccountReport", true);
                pModel.addAttribute("totalRoleDataDeleteAccountReport", crsDeleteImportModel.size());
                pModel.addAttribute("userRoleDataDeleteAccountReport", crsDeleteImportModel);
            }

            //Delete TIN Individual
            List<CRSImportDeleteCustodyTinIndividualModel> tINIndividualDelete = new ArrayList<>();
            tINIndividualDelete = importDeleteCustodyTinIndividualRepository.getAllApproval();
            if (tINIndividualDelete.isEmpty()) {
                pModel.addAttribute("alreadyDataTinIndividualDelete", false);
            } else {
                pModel.addAttribute("alreadyDataTinIndividualDelete", true);
                pModel.addAttribute("totalRoleDataTinIndividualDelete", tINIndividualDelete.size());
                pModel.addAttribute("userRoleDataTinIndividualDelete", tINIndividualDelete);
            }

            //Delete In Organization
            List<CRSImportDeleteCustodyInOrganizationModel> inOrganizationDelete = new ArrayList<>();
            inOrganizationDelete = importDeleteCustodyInOrganizationRepository.getAllApproval();
            if (inOrganizationDelete.isEmpty()) {
                pModel.addAttribute("alreadyDatainOrganizationDelete", false);
            } else {
                pModel.addAttribute("alreadyDatainOrganizationDelete", true);
                pModel.addAttribute("totalRoleDatainOrganizationDelete", inOrganizationDelete.size());
                pModel.addAttribute("userRoleDatainOrganizationDelete", inOrganizationDelete);
            }

            //Delete Name Individual
            List<CRSImportDeleteCustodyNameIndividualModel> nameIndividualDelete = new ArrayList<>();
            nameIndividualDelete = importDeleteCustodyNameIndividualRepository.getAllApproval();
            if (nameIndividualDelete.isEmpty()) {
                pModel.addAttribute("alreadyDatanameIndividualDelete", false);
            } else {
                pModel.addAttribute("alreadyDatanameIndividualDelete", true);
                pModel.addAttribute("totalRoleDatanameIndividualDelete", nameIndividualDelete.size());
                pModel.addAttribute("userRoleDatanameIndividualDelete", nameIndividualDelete);
            }

            // Delete Name Organization
            List<CRSImportDeleteCustodyNameOrganizationModel> nameOrganizationDelete = new ArrayList<>();
            nameOrganizationDelete = importDeleteCustodyNameOrganizationRepository.getAllApproval();
            if (nameOrganizationDelete.isEmpty()) {
                pModel.addAttribute("alreadyDatanameOrganizationDelete", false);
            } else {
                pModel.addAttribute("alreadyDatanameOrganizationDelete", true);
                pModel.addAttribute("totalRoleDatanameOrganizationDelete", nameOrganizationDelete.size());
                pModel.addAttribute("userRoleDatanameOrganizationDelete", nameOrganizationDelete);
            }

            //Delete Rep Address
            List<CRSImportDeleteCustodyRepAddressModel> repAddressDelete = new ArrayList<>();
            repAddressDelete = importDeleteCustodyRepAddressRepository.getAllApproval();
            if (repAddressDelete.isEmpty()) {
                pModel.addAttribute("alreadyDatarepAddressDelete", false);
            } else {
                pModel.addAttribute("alreadyDatarepAddressDelete", true);
                pModel.addAttribute("totalRoleDatarepAddressDelete", repAddress.size());
                pModel.addAttribute("userRoleDatarepAddressDelete", repAddressDelete);
            }

            //Delete Rep Payment
            List<CRSImportDeleteCustodyRepPaymentModel> repPaymentDelete = new ArrayList<>();
            repPaymentDelete = importDeleteCustodyRepPaymentRepository.getAllApproval();
            if (repPaymentDelete.isEmpty()) {
                pModel.addAttribute("alreadyDatarepPaymentDelete", false);
            } else {
                pModel.addAttribute("alreadyDatarepPaymentDelete", true);
                pModel.addAttribute("totalRoleDatarepPaymentDelete", repPaymentDelete.size());
                pModel.addAttribute("userRoleDatarepPaymentDelete", repPaymentDelete);
            }


            pModel.addAttribute("contentPath", "uploadapprovalcrscustody");
            pModel.addAttribute("contentName", "uploadapprovalcrscustody");
            UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
            pModel.addAttribute("var",userRole.getRoleId());
            List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
            pModel.addAttribute("roleUserAccess", userAccess);
            pModel.addAttribute("UserLogin",userRole.getName());
            pModel.addAttribute("User",userLoginApp);

            auditTrailCustodyService.postAuditTrail(userLoginApp,Operation,AccessScreen,"","");
            return "index";

        } else {
            log.info("Cookies Else");
            return "redirect:/login";
        }
    }

    @GetMapping("approval/koreksidjp")
    public String getApprovalKoreksiDjp(RoleModel pRoleModel, HttpServletRequest pRequest, Model pModel)
            throws IOException, JAXBException, ParseException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());

        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "View";
        String AccessScreen     = "View Approval Koreksi Pelaporan Domestik Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);



        if (cookies != null) {
            log.info("Cookies List : " + Arrays.stream(cookies)
                    .map(c -> c.getName() + "=" + c.getValue()).collect(Collectors.joining(", ")));

            //get Personal
            List<DJPImportCustodyKoreksiDJPModel> dJPImportKoreksiDJPModel = new ArrayList<>();
            dJPImportKoreksiDJPModel = importCustodyKoreksiDjpRepository.getAllUpload();

            if (dJPImportKoreksiDJPModel.isEmpty()) {
                pModel.addAttribute("alreadyData", false);
            } else {
                pModel.addAttribute("alreadyData", true);
                pModel.addAttribute("totalRoleData", dJPImportKoreksiDJPModel.size());
                pModel.addAttribute("userRoleData", dJPImportKoreksiDJPModel);
            }

            //get Non Personal
            List<DJPImportCustodyPengendaliEntitasKoreksiDjpModel> dJPImportPengendaliEntitasKoreksiDjpModel = new ArrayList<>();
            dJPImportPengendaliEntitasKoreksiDjpModel = importCustodyPengendaliEntitasKoreksiDjpRepository.getAllUploadApproval();

            if (dJPImportPengendaliEntitasKoreksiDjpModel.isEmpty()) {
                pModel.addAttribute("alreadyDataEntitas", false);
            } else {
                pModel.addAttribute("alreadyDataEntitas", true);
                pModel.addAttribute("totalRoleDataEntitas", dJPImportPengendaliEntitasKoreksiDjpModel.size());
                pModel.addAttribute("userRoleDataEntitas", dJPImportPengendaliEntitasKoreksiDjpModel);
            }

            pModel.addAttribute("contentPath", "uploadapprovalorrkoreksidjpcustody");
            pModel.addAttribute("contentName", "uploadapprovalorrkoreksidjpcustody");
            UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
            pModel.addAttribute("var",userRole.getRoleId());
            List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
            pModel.addAttribute("roleUserAccess", userAccess);
            pModel.addAttribute("UserLogin",userRole.getName());
            pModel.addAttribute("User",userLoginApp);

            auditTrailCustodyService.postAuditTrail(userLoginApp,Operation,AccessScreen,"","");
            return "index";

        } else {
            log.info("Cookies Else");
            return "redirect:/login";
        }
    }

    @GetMapping("approvaldjp/crs")
    public String getApprovalDjpCrs(RoleModel pRoleModel, HttpServletRequest pRequest, Model pModel)
            throws IOException, JAXBException, ParseException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());

        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "View";
        String AccessScreen     = "View Approval Koreksi Pelaporan CRS Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);


        if (cookies != null) {
            log.info("Cookies List : " + Arrays.stream(cookies)
                    .map(c -> c.getName() + "=" + c.getValue()).collect(Collectors.joining(", ")));

            //Account Report
            List<CRSImportCustodyDjpModel> crsImportDjpModel = new ArrayList<>();
            crsImportDjpModel = importCustodyAccountReportDjpRepository.getAllApproval();
            if (crsImportDjpModel.isEmpty()) {
                pModel.addAttribute("alreadyData", false);
            } else {
                pModel.addAttribute("alreadyData", true);
                pModel.addAttribute("totalRoleData", crsImportDjpModel.size());
                pModel.addAttribute("userRoleData", crsImportDjpModel);
            }

            //TIN Individual
            List<CRSImportCustodyTinIndividualDjpModel> tINIndividualDjp = new ArrayList<>();
            tINIndividualDjp = importCustodyTinIndividualDjpRepository.getAllApproval();
            if (tINIndividualDjp.isEmpty()) {
                pModel.addAttribute("alreadyDataTinIndividual", false);
            } else {
                pModel.addAttribute("alreadyDataTinIndividual", true);
                pModel.addAttribute("totalRoleDataTinIndividual", tINIndividualDjp.size());
                pModel.addAttribute("userRoleDataTinIndividual", tINIndividualDjp);
            }

            //In Organization
            List<CRSImportCustodyInOrganizationDjpModel> inOrganizationDjp = new ArrayList<>();
            inOrganizationDjp = importCustodyInOrganizationDjpRepository.getAllApproval();
            if (inOrganizationDjp.isEmpty()) {
                pModel.addAttribute("alreadyDatainOrganization", false);
            } else {
                pModel.addAttribute("alreadyDatainOrganization", true);
                pModel.addAttribute("totalRoleDatainOrganization", inOrganizationDjp.size());
                pModel.addAttribute("userRoleDatainOrganization", inOrganizationDjp);
            }

            //Name Individual
            List<CRSImportCustodyNameIndividualDjpModel> nameIndividualDjp = new ArrayList<>();
            nameIndividualDjp = importCustodyNameIndividualDjpRepository.getAllApproval();
            if (nameIndividualDjp.isEmpty()) {
                pModel.addAttribute("alreadyDatanameIndividual", false);
            } else {
                pModel.addAttribute("alreadyDatanameIndividual", true);
                pModel.addAttribute("totalRoleDatanameIndividual", nameIndividualDjp.size());
                pModel.addAttribute("userRoleDatanameIndividual", nameIndividualDjp);
            }

            //Name Organization
            List<CRSImportCustodyNameOrganizationDjpModel> nameOrganizationDjp = new ArrayList<>();
            nameOrganizationDjp = importCustodyNameOrganizationDjpRepository.getAllApproval();
            if (nameOrganizationDjp.isEmpty()) {
                pModel.addAttribute("alreadyDatanameOrganization", false);
            } else {
                pModel.addAttribute("alreadyDatanameOrganization", true);
                pModel.addAttribute("totalRoleDatanameOrganization", nameOrganizationDjp.size());
                pModel.addAttribute("userRoleDatanameOrganization", nameOrganizationDjp);
            }

            //Rep Address
            List<CRSImportCustodyRepAddressDjpModel> repAddressDjp = new ArrayList<>();
            repAddressDjp = importCustodyRepAddressDjpRepository.getAllApproval();
            if (repAddressDjp.isEmpty()) {
                pModel.addAttribute("alreadyDatarepAddress", false);
            } else {
                pModel.addAttribute("alreadyDatarepAddress", true);
                pModel.addAttribute("totalRoleDatarepAddress", repAddressDjp.size());
                pModel.addAttribute("userRoleDatarepAddress", repAddressDjp);
            }

            //Rep Payment
            List<CRSImportCustodyRepPaymentDjpModel> repPaymentDjp = new ArrayList<>();
            repPaymentDjp = importCustodyRepPaymentDjpRepository.getAllApproval();
            if (repPaymentDjp.isEmpty()) {
                pModel.addAttribute("alreadyDatarepPayment", false);
            } else {
                pModel.addAttribute("alreadyDatarepPayment", true);
                pModel.addAttribute("totalRoleDatarepPayment", repPaymentDjp.size());
                pModel.addAttribute("userRoleDatarepPayment", repPaymentDjp);
            }


            pModel.addAttribute("contentPath", "uploadapprovalcrskoreksidjpcustody");
            pModel.addAttribute("contentName", "uploadapprovalcrskoreksidjpcustody");
            UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
            pModel.addAttribute("var",userRole.getRoleId());
            List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
            pModel.addAttribute("roleUserAccess", userAccess);
            pModel.addAttribute("UserLogin",userRole.getName());
            pModel.addAttribute("User",userLoginApp);

            auditTrailCustodyService.postAuditTrail(userLoginApp,Operation,AccessScreen,"","");
            return "index";

        } else {
            log.info("Cookies Else");
            return "redirect:/login";
        }
    }

    @GetMapping("approval/approvaltemplate/{IdentitasUnik}")
    public String appovalTemplate(@PathVariable("IdentitasUnik") String IdentitasUnik, HttpServletRequest pRequest, Model pModel,
                                  RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve Personal Edit Data Domestik Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);
        int no = 5;

        CustodyDJPResultTTModel before = custodyResult_TTRepository.getfindbyIdentitasUnik(IdentitasUnik);
        DJPImportCustodyModel after = importCustodyRepository.getDataAfter(IdentitasUnik);


        String dataBefore = "1. IdentitasUnik : "+before.getIdentitasUnik()+"\n 2. NIK : "
                +before.getNIKPemegangRek()+"\n 3. CIF : "+before.getNomorCIF()+"\n 4. No Rekening : "
                +before.getNomorRekening()+"\n 5. Nama Pemegang Rekening : "+before.getNamaPemegangRek();

        String dataAfter = "1. IdentitasUnik : "+before.getIdentitasUnik()+"\n 2. NIK : "
                +after.getNIKPemegangRek_imp()+"\n 3. CIF : "+after.getNomorCIF_imp()+"\n 4. No Rekening : "
                +after.getNomorRekening_imp()+"\n 5. Nama Pemegang Rekening : "+after.getNamaPemegangRek_imp();

        if(!before.getNPWPLembagaKeuanganPengirim().equals(after.getNPWPLembagaKeuanganPengirim_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". NPWP Lembaga Keuangan Pengirim : "+before.getNPWPLembagaKeuanganPengirim();
            dataAfter  = dataAfter + "\n "+no+". NPWP Lembaga Keuangan Pengirim : "+after.getNPWPLembagaKeuanganPengirim_imp();
        }

        if(!before.getNPWPLembagaKeuanganPelapor().equals(after.getNPWPLembagaKeuanganPelapor_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". NPWP Lembaga Keuangan Pelapor : "+before.getNPWPLembagaKeuanganPelapor();
            dataAfter  = dataAfter + "\n "+no+". NPWP Lembaga Keuangan Pelapor : "+after.getNPWPLembagaKeuanganPelapor_imp();
        }

        if(!before.getJenisData().equals(after.getJenisData_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Jenis Data : "+before.getJenisData();
            dataAfter  = dataAfter + "\n "+no+". Jenis Data : "+after.getJenisData_imp();
        }

        if(!before.getJenisLembagaKeuangan().equals(after.getJenisLembagaKeuangan_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Jenis Lembaga Keuangan : "+before.getJenisData();
            dataAfter = dataAfter + "\n "+no+". Jenis Lembaga Keuangan : "+after.getJenisData_imp();
        }

        if(!before.getNomorCifNomorRekening().equals(after.getNomorCifNomorRekening_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Nomer CIF Nomer Rekening : "+before.getNomorCifNomorRekening();
            dataAfter = dataAfter + "\n "+no+". Nomer CIF Nomer Rekening : "+after.getNomorCifNomorRekening_imp();
        }

        if(!before.getStsRekening().equals(after.getStsRekening_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Status Rekening : "+before.getStsRekening();
            dataAfter = dataAfter + "\n "+no+". Status Rekening : "+after.getStsRekening_imp();
        }

        if(!before.getJnsPemegangRekening().equals(after.getJnsPemegangRekening_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Jenis Pemegang Rekening : "+before.getJnsPemegangRekening();
            dataAfter = dataAfter + "\n "+no+". Jenis Pemegang Rekening : "+after.getJnsPemegangRekening_imp();
        }

        if(!before.getMataUang().equals(after.getMataUang_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Mata Uang : "+before.getMataUang();
            dataAfter = dataAfter + "\n "+no+". Mata Uang: "+after.getMataUang_imp();
        }

        if(!twoPlaces.format(before.getSaldoAtauNilai()).equals(after.getSaldoAtauNilai_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Saldo Atau Nilai : "+twoPlaces.format(before.getSaldoAtauNilai());
            dataAfter = dataAfter + "\n "+no+". Saldo Atau Nilai : "+after.getSaldoAtauNilai_imp();
        }

        if(!twoPlaces.format(before.getDeviden()).equals(after.getDeviden_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Deviden : "+twoPlaces.format(before.getDeviden());
            dataAfter = dataAfter + "\n "+no+". Deviden : "+after.getDeviden_imp();
        }

        if(!twoPlaces.format(before.getCouponPayment()).equals(after.getCouponPayment_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Coupon Payment : "+twoPlaces.format(before.getCouponPayment());
            dataAfter = dataAfter + "\n "+no+". Coupon Payment : "+after.getCouponPayment_imp();
        }

        if(!twoPlaces.format(before.getPhBruto()).equals(after.getPhBruto_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". PH Bruto : "+twoPlaces.format(before.getPhBruto());
            dataAfter = dataAfter + "\n "+no+". PH Bruto : "+after.getPhBruto_imp();
        }

        if(!twoPlaces.format(before.getPhLainnya()).equals(after.getPhLainnya_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". PH Lainnya : "+twoPlaces.format(before.getPhLainnya());
            dataAfter = dataAfter + "\n "+no+". PH Lainnya : "+after.getPhLainnya_imp();
        }

        if(!before.getNamaLainPemegangRek().equals(after.getNamaLainPemegangRek_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Nama Lain Pemegang Rekening : "+before.getNamaLainPemegangRek();
            dataAfter = dataAfter + "\n "+no+". Nama Lain Pemegang Rekening : "+after.getNamaLainPemegangRek_imp();
        }

        if(!before.getNPWPPemegangRek().equals(after.getNPWPPemegangRek_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". NPWP Pemegang Rekening : "+before.getNPWPPemegangRek();
            dataAfter = dataAfter + "\n "+no+". NPWP Pemegang Rekening : "+after.getNPWPPemegangRek_imp();
        }

        if(!before.getSIMPemegangRek().equals(after.getSIMPemegangRek_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". SIM Pemegang Rekening : "+before.getSIMPemegangRek();
            dataAfter = dataAfter + "\n "+no+". SIM Pemegang Rekening : "+after.getSIMPemegangRek_imp();
        }

        if(!before.getPasporPemegangRek().equals(after.getPasporPemegangRek_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Paspor Pemegang Rekening : "+before.getPasporPemegangRek();
            dataAfter = dataAfter + "\n "+no+". Paspor Pemegang Rekening : "+after.getPasporPemegangRek_imp();
        }

        if(!before.getSIUPPemegangRek().equals(after.getSIUPPemegangRek_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". SIUP Pemegang Rekening : "+before.getSIUPPemegangRek();
            dataAfter = dataAfter + "\n "+no+". SIUP Pemegang Rekening : "+after.getSIUPPemegangRek_imp();
        }

        if(!before.getSITUPemegangRek().equals(after.getSITUPemegangRek_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". SITU Pemegang Rekening : "+before.getSITUPemegangRek();
            dataAfter = dataAfter + "\n "+no+". SITU Pemegang Rekening : "+after.getSITUPemegangRek_imp();
        }

        if(!before.getAktaPemegangRek().equals(after.getAktaPemegangRek_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Akta Pemegang Rekening : "+before.getAktaPemegangRek();
            dataAfter = dataAfter + "\n "+no+". Akta Pemegang Rekening : "+after.getAktaPemegangRek_imp();
        }

        if(!before.getKewarganegaraanPemegangRek().equals(after.getKewarganegaraanPemegangRek_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Kewarganegaraan Pemegang Rekening : "+before.getKewarganegaraanPemegangRek();
            dataAfter = dataAfter + "\n "+no+". Kewarganegaraan Pemegang Rekening : "+after.getKewarganegaraanPemegangRek_imp();
        }

        if(!before.getTempatLahirPemegangRek().equals(after.getTempatLahirPemegangRek_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Tempat Lahir Pemegang Rekening : "+before.getTempatLahirPemegangRek();
            dataAfter = dataAfter + "\n "+no+". Tempat Lahir Pemegang Rekening : "+after.getTempatLahirPemegangRek_imp();
        }

        if(!before.getTglLahirPemegangRek().equals(after.getTglLahirPemegangRek_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Tanggal Lahir Pemegang Rekening : "+before.getTglLahirPemegangRek();
            dataAfter = dataAfter + "\n "+no+". Tanggal Lahir Pemegang Rekening : "+after.getTglLahirPemegangRek_imp();
        }

        if(!before.getAlamatDomPemegangRek().equals(after.getAlamatDomPemegangRek_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Alamat DOM Pemegang Rekening : "+before.getAlamatDomPemegangRek();
            dataAfter = dataAfter + "\n "+no+". Alamat DOM Pemegang Rekening : "+after.getAlamatDomPemegangRek_imp();
        }

        if(!before.getAlamatUsahaPemegangRek().equals(after.getAlamatUsahaPemegangRek_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Alamat Usaha Pemegang Rekening : "+before.getAlamatUsahaPemegangRek();
            dataAfter = dataAfter + "\n "+no+". Alamat Usaha Pemegang Rekening : "+after.getAlamatUsahaPemegangRek_imp();
        }

        if(!before.getAlamatKorespondensiPemegangRek().equals(after.getAlamatKorespondensiPemegangRek_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Alamat Korespondensi Pemegang Rekening : "+before.getAlamatKorespondensiPemegangRek();
            dataAfter = dataAfter + "\n "+no+". Alamat Korespondensi Pemegang Rekening : "+after.getAlamatKorespondensiPemegangRek_imp();
        }

        if (cookies != null) {
            auditTrailCustodyService.postAuditTrail(userLoginApp, Operation, AccessScreen,dataBefore,dataAfter);
        }

        custodyApprovalService.approveTemplateDJP(IdentitasUnik);

        redirectAttr.addFlashAttribute("Success","Success!, The corrected file with identitasunik "+IdentitasUnik+" has been approved.");


        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approval";
    }

    @GetMapping("approval/approvaltemplatedelete/{IdentitasUnik}")
    public String appovalTemplateDelete(@PathVariable("IdentitasUnik") String IdentitasUnik, HttpServletRequest pRequest, Model pModel,
                                  RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve Delete Personal Edit Data Domestik Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);
        int no = 5;

        CustodyDJPResultTTModel before = custodyResult_TTRepository.getfindbyIdentitasUnik(IdentitasUnik);
        DJPImportDeleteCustodyModel after = importCustodyDeleteRepository.getDataAfter(IdentitasUnik);


        String dataBefore = "1. IdentitasUnik : "+before.getIdentitasUnik()+"\n 2. NIK : "
                +before.getNIKPemegangRek()+"\n 3. CIF : "+before.getNomorCIF()+"\n 4. No Rekening : "
                +before.getNomorRekening()+"\n 5. Nama Pemegang Rekening : "+before.getNamaPemegangRek();

        String dataAfter = "1. IdentitasUnik : "+before.getIdentitasUnik()+"\n 2. NIK : "
                +before.getNIKPemegangRek()+"\n 3. CIF : "+before.getNomorCIF()+"\n 4. No Rekening : "
                +before.getNomorRekening()+"\n 5. Nama Pemegang Rekening : "+before.getNamaPemegangRek();

        if(!before.getJenisData().equals(after.getJenisData_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Jenis Data : "+before.getJenisData();
            dataAfter  = dataAfter + "\n "+no+". Jenis Data : "+after.getJenisData_imp();
        }

        if(!before.getJenisLembagaKeuangan().equals(after.getJenisLembagaKeuangan_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Jenis Lembaga Keuangan : "+before.getJenisData();
            dataAfter = dataAfter + "\n "+no+". Jenis Lembaga Keuangan : "+after.getJenisData_imp();
        }

        if(!before.getNomorCifNomorRekening().equals(after.getNomorCifNomorRekening_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Nomer CIF Nomer Rekening : "+before.getNomorCifNomorRekening();
            dataAfter = dataAfter + "\n "+no+". Nomer CIF Nomer Rekening : "+after.getNomorCifNomorRekening_imp();
        }
        if(!before.getStsRekening().equals(after.getStsRekening_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Status Rekening : "+before.getStsRekening();
            dataAfter = dataAfter + "\n "+no+". Status Rekening : "+after.getStsRekening_imp();
        }

        if(!before.getJnsPemegangRekening().equals(after.getJnsPemegangRekening_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Jenis Pemegang Rekening : "+before.getJnsPemegangRekening();
            dataAfter = dataAfter + "\n "+no+". Jenis Pemegang Rekening : "+after.getJnsPemegangRekening_imp();
        }

        if(!before.getMataUang().equals(after.getMataUang_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Mata Uang : "+before.getMataUang();
            dataAfter = dataAfter + "\n "+no+". Mata Uang: "+after.getMataUang_imp();
        }

        if(!twoPlaces.format(before.getSaldoAtauNilai()).equals(after.getSaldoAtauNilai_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Saldo Atau Nilai : "+twoPlaces.format(before.getSaldoAtauNilai());
            dataAfter = dataAfter + "\n "+no+". Saldo Atau Nilai : "+after.getSaldoAtauNilai_imp();
        }

        if(!twoPlaces.format(before.getDeviden()).equals(after.getDeviden_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Deviden : "+twoPlaces.format(before.getDeviden());
            dataAfter = dataAfter + "\n "+no+". Deviden : "+after.getDeviden_imp();
        }

        if(!twoPlaces.format(before.getCouponPayment()).equals(after.getCouponPayment_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Coupon Payment : "+twoPlaces.format(before.getCouponPayment());
            dataAfter = dataAfter + "\n "+no+". Coupon Payment : "+after.getCouponPayment_imp();
        }

        if(!twoPlaces.format(before.getPhBruto()).equals(after.getPhBruto_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". PH Bruto : "+twoPlaces.format(before.getPhBruto());
            dataAfter = dataAfter + "\n "+no+". PH Bruto : "+after.getPhBruto_imp();
        }

        if(!twoPlaces.format(before.getPhLainnya()).equals(after.getPhLainnya_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". PH Lainnya : "+twoPlaces.format(before.getPhLainnya());
            dataAfter = dataAfter + "\n "+no+". PH Lainnya : "+after.getPhLainnya_imp();
        }

        if(!before.getNamaLainPemegangRek().equals(after.getNamaLainPemegangRek_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Nama Lain Pemegang Rekening : "+before.getNamaLainPemegangRek();
            dataAfter = dataAfter + "\n "+no+". Nama Lain Pemegang Rekening : "+after.getNamaLainPemegangRek_imp();
        }

        if(!before.getNPWPPemegangRek().equals(after.getNPWPPemegangRek_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". NPWP Pemegang Rekening : "+before.getNPWPPemegangRek();
            dataAfter = dataAfter + "\n "+no+". NPWP Pemegang Rekening : "+after.getNPWPPemegangRek_imp();
        }

        if(!before.getSIMPemegangRek().equals(after.getSIMPemegangRek_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". SIM Pemegang Rekening : "+before.getSIMPemegangRek();
            dataAfter = dataAfter + "\n "+no+". SIM Pemegang Rekening : "+after.getSIMPemegangRek_imp();
        }

        if(!before.getPasporPemegangRek().equals(after.getPasporPemegangRek_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Paspor Pemegang Rekening : "+before.getPasporPemegangRek();
            dataAfter = dataAfter + "\n "+no+". Paspor Pemegang Rekening : "+after.getPasporPemegangRek_imp();
        }

        if(!before.getSIUPPemegangRek().equals(after.getSIUPPemegangRek_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". SIUP Pemegang Rekening : "+before.getSIUPPemegangRek();
            dataAfter = dataAfter + "\n "+no+". SIUP Pemegang Rekening : "+after.getSIUPPemegangRek_imp();
        }

        if(!before.getSITUPemegangRek().equals(after.getSITUPemegangRek_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". SITU Pemegang Rekening : "+before.getSITUPemegangRek();
            dataAfter = dataAfter + "\n "+no+". SITU Pemegang Rekening : "+after.getSITUPemegangRek_imp();
        }

        if(!before.getAktaPemegangRek().equals(after.getAktaPemegangRek_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Akta Pemegang Rekening : "+before.getAktaPemegangRek();
            dataAfter = dataAfter + "\n "+no+". Akta Pemegang Rekening : "+after.getAktaPemegangRek_imp();
        }

        if(!before.getKewarganegaraanPemegangRek().equals(after.getKewarganegaraanPemegangRek_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Kewarganegaraan Pemegang Rekening : "+before.getKewarganegaraanPemegangRek();
            dataAfter = dataAfter + "\n "+no+". Kewarganegaraan Pemegang Rekening : "+after.getKewarganegaraanPemegangRek_imp();
        }

        if(!before.getTempatLahirPemegangRek().equals(after.getTempatLahirPemegangRek_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Tempat Lahir Pemegang Rekening : "+before.getTempatLahirPemegangRek();
            dataAfter = dataAfter + "\n "+no+". Tempat Lahir Pemegang Rekening : "+after.getTempatLahirPemegangRek_imp();
        }

        if(!before.getTglLahirPemegangRek().equals(after.getTglLahirPemegangRek_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Tanggal Lahir Pemegang Rekening : "+before.getTglLahirPemegangRek();
            dataAfter = dataAfter + "\n "+no+". Tanggal Lahir Pemegang Rekening : "+after.getTglLahirPemegangRek_imp();
        }


        if(!before.getAlamatDomPemegangRek().equals(after.getAlamatDomPemegangRek_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Alamat DOM Pemegang Rekening : "+before.getAlamatDomPemegangRek();
            dataAfter = dataAfter + "\n "+no+". Alamat DOM Pemegang Rekening : "+after.getAlamatDomPemegangRek_imp();
        }

        if(!before.getAlamatUsahaPemegangRek().equals(after.getAlamatUsahaPemegangRek_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Alamat Usaha Pemegang Rekening : "+before.getAlamatUsahaPemegangRek();
            dataAfter = dataAfter + "\n "+no+". Alamat Usaha Pemegang Rekening : "+after.getAlamatUsahaPemegangRek_imp();
        }

        if(!before.getAlamatKorespondensiPemegangRek().equals(after.getAlamatKorespondensiPemegangRek_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Alamat Korespondensi Pemegang Rekening : "+before.getAlamatKorespondensiPemegangRek();
            dataAfter = dataAfter + "\n "+no+". Alamat Korespondensi Pemegang Rekening : "+after.getAlamatKorespondensiPemegangRek_imp();
        }

        if (cookies != null) {
            auditTrailCustodyService.postAuditTrail(userLoginApp, Operation, AccessScreen,dataBefore,dataAfter);
        }

        custodyApprovalService.approveTemplateDJPDelete(IdentitasUnik);

        redirectAttr.addFlashAttribute("SuccessDelete","Success!, The deleted file with identitasunik "+IdentitasUnik+" has been approved.");


        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approval#HapusPersonal";
    }


    @GetMapping("approval/approvaltemplate/entitas/{IdentitasUnik}")
    public String appovalTemplateEntitas(@PathVariable("IdentitasUnik") String IdentitasUnik, HttpServletRequest pRequest, Model pModel,
                                         RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve Non Personal Edit Data Domestik Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);
        int no = 5;

        CustodyDJPResultTTModel before = custodyResult_TTRepository.getfindbyIdentitasUnik(IdentitasUnik);
        DJPImportCustodyPengendaliEntitasModel after = importCustodyPengendaliEntitasRepository.getDataAfter(IdentitasUnik);

        String dataBefore = "1. IdentitasUnik : "+before.getIdentitasUnik()+"\n 2. NIK : "
                +before.getNIKPemegangRek()+"\n 3. CIF : "+before.getNomorCIF()+"\n 4. No Rekening : "
                +before.getNomorRekening()+"\n 5. Nama Pemegang Rekening : "+before.getNamaPemegangRek();

        String dataAfter = "1. IdentitasUnik : "+before.getIdentitasUnik()+"\n 2. NIK : "
                +before.getNIKPemegangRek()+"\n 3. CIF : "+before.getNomorCIF()+"\n 4. No Rekening : "
                +before.getNomorRekening()+"\n 5. Nama Pemegang Rekening : "+before.getNamaPemegangRek();

        if(!before.getNAMA_DIR().equals(after.getNamaCP_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Nama CP : " + before.getNAMA_DIR();
            dataAfter = dataAfter + "\n "+no+". Nama CP : "+after.getNamaCP_imp();
        }

        if(!before.getKD_NEGARA_CP().equals(after.getKodeNegaraCP_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Kode Negara CP : " + before.getKD_NEGARA_CP();
            dataAfter = dataAfter + "\n "+no+". Kode Negara CP : "+after.getKodeNegaraCP_imp();
        }

        if(!before.getALAMAT_DOM_CP().equals(after.getAlamatDomCP_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Alamat DOM CP : " + before.getALAMAT_DOM_CP();
            dataAfter = dataAfter + "\n "+no+". Alamat DOM CP : "+after.getAlamatDomCP_imp();
        }

        if (cookies != null) {
            auditTrailCustodyService.postAuditTrail(userLoginApp, Operation, AccessScreen,dataBefore,dataAfter);
        }

        custodyApprovalService.approveTemplateEntitasDJP(IdentitasUnik);

        redirectAttr.addFlashAttribute("SuccessEntitas","Success!, The corrected file with identitasunik "+IdentitasUnik+" has been approved.");


        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approval#NonPersonal";
    }

    @GetMapping("approval/approvaltemplatedelete/entitas/{IdentitasUnik}")
    public String appovalTemplateEntitasDelete(@PathVariable("IdentitasUnik") String IdentitasUnik, HttpServletRequest pRequest, Model pModel,
                                               RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve Delete Non Personal Edit Data Domestik Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);
        int no = 5;

        CustodyDJPResultTTModel before = custodyResult_TTRepository.getfindbyIdentitasUnik(IdentitasUnik);
        DJPImportDeleteCustodyPengendaliEntitasModel after = importCustodyPengendaliEntitasDeleteRepository.getDataAfter(IdentitasUnik);

        String dataBefore = "1. IdentitasUnik : "+before.getIdentitasUnik()+"\n 2. NIK : "
                +before.getNIKPemegangRek()+"\n 3. CIF : "+before.getNomorCIF()+"\n 4. No Rekening : "
                +before.getNomorRekening()+"\n 5. Nama Pemegang Rekening : "+before.getNamaPemegangRek();

        String dataAfter = "1. IdentitasUnik : "+before.getIdentitasUnik()+"\n 2. NIK : "
                +before.getNIKPemegangRek()+"\n 3. CIF : "+before.getNomorCIF()+"\n 4. No Rekening : "
                +before.getNomorRekening()+"\n 5. Nama Pemegang Rekening : "+before.getNamaPemegangRek();

        if(!before.getNAMA_DIR().equals(after.getNamaCP_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Nama CP : " + before.getNAMA_DIR();
            dataAfter = dataAfter + "\n "+no+". Nama CP : "+after.getNamaCP_imp();
        }

        if(!before.getKD_NEGARA_CP().equals(after.getKodeNegaraCP_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Kode Negara CP : " + before.getKD_NEGARA_CP();
            dataAfter = dataAfter + "\n "+no+". Kode Negara CP : "+after.getKodeNegaraCP_imp();
        }

        if(!before.getALAMAT_DOM_CP().equals(after.getAlamatDomCP_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Alamat DOM CP : " + before.getALAMAT_DOM_CP();
            dataAfter = dataAfter + "\n "+no+". Alamat DOM CP : "+after.getAlamatDomCP_imp();
        }

        if (cookies != null) {
            auditTrailCustodyService.postAuditTrail(userLoginApp, Operation, AccessScreen,dataBefore,dataAfter);
        }

        custodyApprovalService.approveTemplateEntitasDJPDelete(IdentitasUnik);

        redirectAttr.addFlashAttribute("SuccessEntitasDelete","Success!, The corrected file with identitasunik "+IdentitasUnik+" has been approved.");


        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approval#HapusNonPersonal";
    }

    @GetMapping("approval/approvaldjp/{IdentitasUnik}")
    public String appovalDjp(@PathVariable("IdentitasUnik") String IdentitasUnik, HttpServletRequest pRequest, Model pModel,
                             RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve Personal Koreksi Pelaporan Domestik Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);
        int no = 5;

        DJPDomestikModel djpDomestikModel = custodyApprovalService.approveDJPKoreksi(IdentitasUnik);
        CustodyDJPResultTTModel before = custodyResult_TTRepository.getfindbyIdentitasUnik(IdentitasUnik);

        String dataBefore = "1. IdentitasUnik : "+before.getIdentitasUnik()+"\n 2. NIK : "
                +before.getNIKPemegangRek()+"\n 3. CIF : "+before.getNomorCIF()+"\n 4. No Rekening : "
                +before.getNomorRekening()+"\n 5. Nama Pemegang Rekening : "+before.getNamaPemegangRek();

        String dataAfter = "1. IdentitasUnik : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getIdentitasUnik()+
                "\n 2. NIK : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getNIKPemegangRek()+
                "\n 3. CIF : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getNomorCIF()+
                "\n 4. No Rekening : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getNomorRekening()+
                "\n 5. Nama Pemegang Rekening : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getNamaPemegangRek();

        if(!before.getNPWPLembagaKeuanganPengirim().equals(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getNPWPLembagaKeuanganPelapor())){
            no++;
            dataBefore = dataBefore + "\n "+no+". NPWP Lembaga Keuangan Pengirim : " + before.getNPWPLembagaKeuanganPengirim();
            dataAfter  = dataAfter + "\n "+no+". NPWP Lembaga Keuangan Pengirim : " + djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getNPWPLembagaKeuanganPelapor();
        }

        if(!before.getNPWPLembagaKeuanganPelapor().equals(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getNPWPLembagaKeuanganPelapor())){
            no++;
            dataBefore = dataBefore + "\n "+no+". NPWP Lembaga Keuangan Pelapor : " + before.getNPWPLembagaKeuanganPelapor();
            dataAfter  = dataAfter + "\n "+no+". NPWP Lembaga Keuangan Pelapor : " + djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getNPWPLembagaKeuanganPelapor();
        }

        if(!before.getJenisData().equals(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getJenisData())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Jenis Data : "+before.getJenisData();
            dataAfter  = dataAfter + "\n "+no+". Jenis Data : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getJenisData();
        }

        if(!before.getJenisLembagaKeuangan().equals(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getJenisLembagaKeuangan())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Jenis Lembaga Keuangan : "+before.getJenisData();
            dataAfter = dataAfter + "\n "+no+". Jenis Lembaga Keuangan : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getJenisLembagaKeuangan();
        }

        if(!before.getNomorCifNomorRekening().equals(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getNomorCifNomorRekening())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Nomer CIF Nomer Rekening : "+before.getNomorCifNomorRekening();
            dataAfter = dataAfter + "\n "+no+". Nomer CIF Nomer Rekening : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getNomorCifNomorRekening();
        }

        if(!before.getStsRekening().equals(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getStsRekening())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Status Rekening : "+before.getStsRekening();
            dataAfter = dataAfter + "\n "+no+". Status Rekening : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getStsRekening();
        }

        if(!before.getJnsPemegangRekening().equals(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getJnsPemegangRekening())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Jenis Pemegang Rekening : "+before.getJnsPemegangRekening();
            dataAfter = dataAfter + "\n "+no+". Jenis Pemegang Rekening : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getJnsPemegangRekening();
        }

        if(!before.getMataUang().equals(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getMataUang())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Mata Uang : "+before.getMataUang();
            dataAfter = dataAfter + "\n "+no+". Mata Uang: "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getMataUang();
        }

        if(!twoPlaces.format(before.getSaldoAtauNilai()).equals(String.valueOf(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getSaldoAtauNilai()))){
            no++;
            dataBefore = dataBefore + "\n "+no+". Saldo Atau Nilai : "+twoPlaces.format(before.getSaldoAtauNilai());
            dataAfter = dataAfter + "\n "+no+". Saldo Atau Nilai : "+String.valueOf(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getSaldoAtauNilai());
        }

        if(!twoPlaces.format(before.getDeviden()).equals(String.valueOf(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getDeviden()))){
            no++;
            dataBefore = dataBefore + "\n "+no+". Deviden : "+twoPlaces.format(before.getDeviden());
            dataAfter = dataAfter + "\n "+no+". Deviden : "+String.valueOf(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getDeviden());
        }

        if(!twoPlaces.format(before.getCouponPayment()).equals(String.valueOf(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getBunga()))){
            no++;
            dataBefore = dataBefore + "\n "+no+". Coupon Payment : "+twoPlaces.format(before.getCouponPayment());
            dataAfter = dataAfter + "\n "+no+". Coupon Payment : "+String.valueOf(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getBunga());
        }

        if(!twoPlaces.format(before.getPhBruto()).equals(String.valueOf(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getPhBruto()))){
            no++;
            dataBefore = dataBefore + "\n "+no+". PH Bruto : "+twoPlaces.format(before.getPhBruto());
            dataAfter = dataAfter + "\n "+no+". PH Bruto : "+String.valueOf(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getPhBruto());
        }

        if(!twoPlaces.format(before.getPhLainnya()).equals(String.valueOf(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getPhLainnya()))){
            no++;
            dataBefore = dataBefore + "\n "+no+". PH Lainnya : "+twoPlaces.format(before.getPhLainnya());
            dataAfter = dataAfter + "\n "+no+". PH Lainnya : "+String.valueOf(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getPhLainnya());
        }

        if(!before.getNamaLainPemegangRek().equals(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getNamaLainPemegangRek())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Nama Lain Pemegang Rekening : "+before.getNamaLainPemegangRek();
            dataAfter = dataAfter + "\n "+no+". Nama Lain Pemegang Rekening : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getNamaLainPemegangRek();
        }

        if(!before.getNPWPPemegangRek().equals(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getNPWPPemegangRek())){
            no++;
            dataBefore = dataBefore + "\n "+no+". NPWP Pemegang Rekening : "+before.getNPWPPemegangRek();
            dataAfter = dataAfter + "\n "+no+". NPWP Pemegang Rekening : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getNPWPPemegangRek();
        }

        if(!before.getSIMPemegangRek().equals(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getSIMPemegangRek())){
            no++;
            dataBefore = dataBefore + "\n "+no+". SIM Pemegang Rekening : "+before.getSIMPemegangRek();
            dataAfter = dataAfter + "\n "+no+". SIM Pemegang Rekening : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getSIMPemegangRek();
        }

        if(!before.getPasporPemegangRek().equals(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getPasporPemegangRek())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Paspor Pemegang Rekening : "+before.getPasporPemegangRek();
            dataAfter = dataAfter + "\n "+no+". Paspor Pemegang Rekening : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getPasporPemegangRek();
        }

        if(!before.getSIUPPemegangRek().equals(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getSIUPPemegangRek())){
            no++;
            dataBefore = dataBefore + "\n "+no+". SIUP Pemegang Rekening : "+before.getSIUPPemegangRek();
            dataAfter = dataAfter + "\n "+no+". SIUP Pemegang Rekening : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getSIUPPemegangRek();
        }

        if(!before.getSITUPemegangRek().equals(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getSITUPemegangRek())){
            no++;
            dataBefore = dataBefore + "\n "+no+". SITU Pemegang Rekening : "+before.getSITUPemegangRek();
            dataAfter = dataAfter + "\n "+no+". SITU Pemegang Rekening : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getSITUPemegangRek();
        }

        if(!before.getAktaPemegangRek().equals(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getAktaPemegangRek())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Akta Pemegang Rekening : "+before.getAktaPemegangRek();
            dataAfter = dataAfter + "\n "+no+". Akta Pemegang Rekening : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getAktaPemegangRek();
        }

        if(!before.getKewarganegaraanPemegangRek().equals(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getKewarganegaraanPemegangRek())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Kewarganegaraan Pemegang Rekening : "+before.getKewarganegaraanPemegangRek();
            dataAfter = dataAfter + "\n "+no+". Kewarganegaraan Pemegang Rekening : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getKewarganegaraanPemegangRek();
        }

        if(!before.getTempatLahirPemegangRek().equals(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getTempatLahirPemegangRek())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Tempat Lahir Pemegang Rekening : "+before.getTempatLahirPemegangRek();
            dataAfter = dataAfter + "\n "+no+". Tempat Lahir Pemegang Rekening : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getTempatLahirPemegangRek();
        }

        if(!before.getTglLahirPemegangRek().equals(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getTglLahirPemegangRek())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Tanggal Lahir Pemegang Rekening : "+before.getTglLahirPemegangRek();
            dataAfter = dataAfter + "\n "+no+". Tanggal Lahir Pemegang Rekening : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getTglLahirPemegangRek();
        }

        if(!before.getAlamatDomPemegangRek().equals(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getAlamatDomPemegangRek())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Alamat DOM Pemegang Rekening : "+before.getAlamatDomPemegangRek();
            dataAfter = dataAfter + "\n "+no+". Alamat DOM Pemegang Rekening : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getAlamatDomPemegangRek();
        }

        if(!before.getAlamatUsahaPemegangRek().equals(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getAlamatUsahaPemegangRek())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Alamat Usaha Pemegang Rekening : "+before.getAlamatUsahaPemegangRek();
            dataAfter = dataAfter + "\n "+no+". Alamat Usaha Pemegang Rekening : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getAlamatUsahaPemegangRek();
        }

        if(!before.getAlamatKorespondensiPemegangRek().equals(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getAlamatKorespondensiPemegangRek())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Alamat Korespondensi Pemegang Rekening : "+before.getAlamatKorespondensiPemegangRek();
            dataAfter = dataAfter + "\n "+no+". Alamat Korespondensi Pemegang Rekening : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getAlamatKorespondensiPemegangRek();
        }

        redirectAttr.addFlashAttribute("Success","Success!, The corrected file with identitasunik "+IdentitasUnik+" has been approved.");



        redirectAttr.addFlashAttribute("Success","Success!, The corrected file with identitasunik "+IdentitasUnik+" has been approved.");

        if (cookies != null) {

            auditTrailCustodyService.postAuditTrail(userLoginApp, Operation, AccessScreen,dataBefore,dataAfter);

        }

        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approval/koreksidjp";
    }

    @GetMapping("approval/approvaldjp/entitas/{IdentitasUnik}")
    public String appovalDjpNonPersonal(@PathVariable("IdentitasUnik") String IdentitasUnik, HttpServletRequest pRequest, Model pModel,
                                        RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve Non Personal Koreksi Pelaporan Domestik Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);
        int no = 5;

        DJPDomestikModel djpDomestikModel = custodyApprovalService.approveDJPKoreksiNonPersonal(IdentitasUnik);


        CustodyDJPResultTTModel before = custodyResult_TTRepository.getfindbyIdentitasUnik(IdentitasUnik);

        String dataBefore = "1. IdentitasUnik : "+before.getIdentitasUnik()+"\n 2. NIK : "
                +before.getNIKPemegangRek()+"\n 3. CIF : "+before.getNomorCIF()+"\n 4. No Rekening : "
                +before.getNomorRekening()+"\n 5. Nama Pemegang Rekening : "+before.getNamaPemegangRek();

        String dataAfter = "1. IdentitasUnik : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getIdentitasUnik()+
                "\n 2. NIK : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getNIKPemegangRek()+
                "\n 3. CIF : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getNomorCIF()+
                "\n 4. No Rekening : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getNomorRekening()+
                "\n 5. Nama Pemegang Rekening : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getNamaPemegangRek();

        if(!before.getNAMA_DIR().equals(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getAlamatDomPemegangRek())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Nama CP : " + before.getNAMA_DIR();
            dataAfter = dataAfter + "\n "+no+". Nama CP : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getAlamatDomPemegangRek();
        }

        if(!before.getKD_NEGARA_CP().equals(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getAlamatUsahaPemegangRek())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Kode Negara CP : " + before.getKD_NEGARA_CP();
            dataAfter = dataAfter + "\n "+no+". Kode Negara CP : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getAlamatUsahaPemegangRek();
        }

        if(!before.getALAMAT_DOM_CP().equals(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getAlamatKorespondensiPemegangRek())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Alamat DOM CP : " + before.getALAMAT_DOM_CP();
            dataAfter = dataAfter + "\n "+no+". Alamat DOM CP : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getAlamatKorespondensiPemegangRek();
        }

        if(!before.getJenisData().equals(djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getJenisData())){
            no++;
            dataBefore = dataBefore + "\n "+no+". Jenis Data : "+before.getJenisData();
            dataAfter  = dataAfter + "\n "+no+". Jenis Data : "+djpDomestikModel.getDjpDomestikBodyModel().getDJPDomestikBodyPersonalModel().stream().iterator().next().getJenisData();
        }

        redirectAttr.addFlashAttribute("SuccessEntitas","Success!, The corrected file with identitasunik "+IdentitasUnik+" has been approved.");

        if (cookies != null) {
            auditTrailCustodyService.postAuditTrail(userLoginApp, Operation, AccessScreen, dataBefore, dataAfter);
        }

        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approval/koreksidjp#NonPersonal";
    }

    @GetMapping("approval/approvaltemplate/crsAccountReport/{DocRefID_AccountReport_Tab1}")
    public String appovalTemplateCrs(@PathVariable("DocRefID_AccountReport_Tab1") String DocRefID_AccountReport_Tab1, HttpServletRequest pRequest, Model pModel,
                                     RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve Account Report Edit Data CRS Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);
        int no = 4;


        CustodyCRSResultTTModel before = cRSCustodyResult_TTRepository.getAllData(DocRefID_AccountReport_Tab1);
        CRSImportCustodyModel after = importCustodyAccountReportRepository.getAllData(DocRefID_AccountReport_Tab1);

        String dataBefore = "1. DocRefID : "+before.getDocRefID_AccountReport_Tab1()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        String dataAfter = "1. DocRefID : "+after.getDocRefID_AccountReport_Tab1()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+after.getAccountNumber_imp()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        if(!before.getUndocumentedAccount().equals(after.getUndocumentedAccount_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". UndocumentedAccount : "+before.getUndocumentedAccount();
            dataAfter  = dataAfter + "\n "+no+". UndocumentedAccount : "+ after.getUndocumentedAccount_imp();
        }

        if(!before.getClosedAccount().equals(after.getClosedAccount_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". ClosedAccount : "+before.getClosedAccount();
            dataAfter  = dataAfter + "\n "+no+". ClosedAccount : "+ after.getClosedAccount_imp();
        }
        if(!before.getDormantAccount().equals(after.getDormantAccount_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". DormantAccount : "+before.getDormantAccount();
            dataAfter  = dataAfter + "\n "+no+". DormantAccount : "+ after.getDormantAccount_imp();
        }
        if(!before.getIsIndividual().equals(after.getIsIndividual_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". IsIndividual : "+before.getIsIndividual();
            dataAfter  = dataAfter + "\n "+no+". IsIndividual : "+ after.getIsIndividual_imp();
        }

        if(!before.getAcctHolderType().equals(after.getAcctHolderType_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". AcctHolderType : "+before.getAcctHolderType();
            dataAfter  = dataAfter + "\n "+no+". AcctHolderType : "+ after.getAcctHolderType_imp();
        }

        if(!before.getResCountryCode().equals(after.getResCountryCode_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". ResCountryCode : "+before.getResCountryCode();
            dataAfter  = dataAfter + "\n "+no+". ResCountryCode : "+ after.getResCountryCode_imp();
        }

        if(!before.getCurrencyCodeAccountBalance().equals(after.getCurrencyCodeAccountBalance_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". CurrencyCodeAccountBalance : "+before.getCurrencyCodeAccountBalance();
            dataAfter  = dataAfter + "\n "+no+". CurrencyCodeAccountBalance : "+ after.getCurrencyCodeAccountBalance_imp();
        }

        if(!twoPlaces.format(before.getAccountBalance()).equals(after.getAccountBalance_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". AccountBalance : "+twoPlaces.format(before.getAccountBalance());
            dataAfter  = dataAfter + "\n "+no+". AccountBalance : "+ after.getAccountBalance_imp();
        }

        if(!before.getBirthInfo_BirthDate().equals(after.getBirthInfo_BirthDate_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". BirthInfo_BirthDate : "+before.getBirthInfo_BirthDate();
            dataAfter  = dataAfter + "\n "+no+". BirthInfo_BirthDate : "+ after.getBirthInfo_BirthDate_imp();
        }

        if (cookies != null) {
            auditTrailCustodyService.postAuditTrail(userLoginApp, Operation, AccessScreen, dataBefore, dataAfter);
        }

        custodyApprovalService.approveTemplateDJPCrsAccountReport(DocRefID_AccountReport_Tab1);

        redirectAttr.addFlashAttribute("Success","Success!, The corrected file with DocRefIdAccountReport "+DocRefID_AccountReport_Tab1+" has been approved.");


        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approval/crs";
    }

    @GetMapping("approval/approvaltemplate/crsDeleteAccountReport/{DocRefID_AccountReport_Tab1}")
    public String appovalTemplateCrsDelete(@PathVariable("DocRefID_AccountReport_Tab1") String DocRefID_AccountReport_Tab1, HttpServletRequest pRequest, Model pModel,
                                     RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve Delete Account Report Edit Data CRS Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);
        int no = 4;


        CustodyCRSResultTTModel before = cRSCustodyResult_TTRepository.getAllData(DocRefID_AccountReport_Tab1);

        CRSImportDeleteCustodyModel after = importDeleteCustodyAccountReportRepository.getAllData(DocRefID_AccountReport_Tab1);

        String dataBefore = "1. DocRefID : "+before.getDocRefID_AccountReport_Tab1()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        String dataAfter = "1. DocRefID : "+after.getDocRefID_AccountReport_Tab1()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        if(!before.getUndocumentedAccount().equals(after.getUndocumentedAccount_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". UndocumentedAccount : "+before.getUndocumentedAccount();
            dataAfter  = dataAfter + "\n "+no+". UndocumentedAccount : "+ after.getUndocumentedAccount_imp();
        }

        if(!before.getClosedAccount().equals(after.getClosedAccount_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". ClosedAccount : "+before.getClosedAccount();
            dataAfter  = dataAfter + "\n "+no+". ClosedAccount : "+ after.getClosedAccount_imp();
        }
        if(!before.getDormantAccount().equals(after.getDormantAccount_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". DormantAccount : "+before.getDormantAccount();
            dataAfter  = dataAfter + "\n "+no+". DormantAccount : "+ after.getDormantAccount_imp();
        }
        if(!before.getIsIndividual().equals(after.getIsIndividual_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". IsIndividual : "+before.getIsIndividual();
            dataAfter  = dataAfter + "\n "+no+". IsIndividual : "+ after.getIsIndividual_imp();
        }

        if(!before.getAcctHolderType().equals(after.getAcctHolderType_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". AcctHolderType : "+before.getAcctHolderType();
            dataAfter  = dataAfter + "\n "+no+". AcctHolderType : "+ after.getAcctHolderType_imp();
        }

        if(!before.getResCountryCode().equals(after.getResCountryCode_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". ResCountryCode : "+before.getResCountryCode();
            dataAfter  = dataAfter + "\n "+no+". ResCountryCode : "+ after.getResCountryCode_imp();
        }

        if(!before.getCurrencyCodeAccountBalance().equals(after.getCurrencyCodeAccountBalance_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". CurrencyCodeAccountBalance : "+before.getCurrencyCodeAccountBalance();
            dataAfter  = dataAfter + "\n "+no+". CurrencyCodeAccountBalance : "+ after.getCurrencyCodeAccountBalance_imp();
        }

        if(!twoPlaces.format(before.getAccountBalance()).equals(after.getAccountBalance_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". AccountBalance : "+twoPlaces.format(before.getAccountBalance());
            dataAfter  = dataAfter + "\n "+no+". AccountBalance : "+ after.getAccountBalance_imp();
        }

        if(!before.getBirthInfo_BirthDate().equals(after.getBirthInfo_BirthDate_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". BirthInfo_BirthDate : "+before.getBirthInfo_BirthDate();
            dataAfter  = dataAfter + "\n "+no+". BirthInfo_BirthDate : "+ after.getBirthInfo_BirthDate_imp();
        }

        if (cookies != null) {
            auditTrailCustodyService.postAuditTrail(userLoginApp, Operation, AccessScreen, dataBefore, dataAfter);
        }

        custodyApprovalService.approveTemplateDJPCrsDeleteAccountReport(DocRefID_AccountReport_Tab1);

        redirectAttr.addFlashAttribute("SuccessDeleteAccountReport","Success!, The deleted file with DocRefIdAccountReport "+DocRefID_AccountReport_Tab1+" has been approved.");


        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approval/crs#DeleteAccountReport";
    }

    @GetMapping("approval/approvaltemplate/crsTinIndividual/{DocRefID_AccountReport_Tab3}")
    public String appovalTemplateCrsTinIndividual(@PathVariable("DocRefID_AccountReport_Tab3") String DocRefID_AccountReport_Tab3, HttpServletRequest pRequest, Model pModel,
                                                  RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve TIN Individual Edit Data CRS Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);
        int no = 4;


        CustodyCRSResultTTModel before = cRSCustodyResult_TTRepository.getAllDataTab3(DocRefID_AccountReport_Tab3);

        CRSImportCustodyTinIndividualModel after = importCustodyTinIndividualRepository.getAllData(DocRefID_AccountReport_Tab3);
        String dataBefore = "1. DocRefID : "+before.getDocRefID_AccountReport_Tab3()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        String dataAfter = "1. DocRefID : "+after.getDocRefID_AccountReport_Tab3()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        if(!before.getTIN().equals(after.getTIN_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". TIN : "+before.getTIN();
            dataAfter  = dataAfter + "\n "+no+". TIN : "+ after.getTIN_imp();
        }

        if(!before.getIssuedBy().equals(after.getIssuedBy_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". IssuedBy : "+before.getIssuedBy();
            dataAfter  = dataAfter + "\n "+no+". IssuedBy : "+ after.getIssuedBy_imp();
        }

        if (cookies != null) {
            auditTrailCustodyService.postAuditTrail(userLoginApp, Operation, AccessScreen, dataBefore, dataAfter);
        }

        custodyApprovalService.approveTemplateDJPCrsTinIndividual(DocRefID_AccountReport_Tab3);
        redirectAttr.addFlashAttribute("SuccessTinIndividual","Success!, The corrected file with DocRefIdAccountReport "+DocRefID_AccountReport_Tab3+" has been approved.");

        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approval/crs#AccountReportTinIndividual";
    }

    @GetMapping("approval/approvaltemplate/crsDeleteTinIndividual/{DocRefID_AccountReport_Tab3}")
    public String appovalTemplateCrsTinIndividualDelete(@PathVariable("DocRefID_AccountReport_Tab3") String DocRefID_AccountReport_Tab3, HttpServletRequest pRequest, Model pModel,
                                                  RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve Delete TIN Individual Edit Data CRS Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);
        int no = 4;

        CustodyCRSResultTTModel before = cRSCustodyResult_TTRepository.getAllDataTab3(DocRefID_AccountReport_Tab3);
        CRSImportDeleteCustodyTinIndividualModel after = importDeleteCustodyTinIndividualRepository.getAllData(DocRefID_AccountReport_Tab3);

        String dataBefore = "1. DocRefID : "+before.getDocRefID_AccountReport_Tab3()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        String dataAfter = "1. DocRefID : "+after.getDocRefID_AccountReport_Tab3()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        if(!before.getTIN().equals(after.getTIN_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". TIN : "+before.getTIN();
            dataAfter  = dataAfter + "\n "+no+". TIN : "+ after.getTIN_imp();
        }

        if(!before.getIssuedBy().equals(after.getIssuedBy_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". IssuedBy : "+before.getIssuedBy();
            dataAfter  = dataAfter + "\n "+no+". IssuedBy : "+ after.getIssuedBy_imp();
        }

        if (cookies != null) {
            auditTrailCustodyService.postAuditTrail(userLoginApp, Operation, AccessScreen, dataBefore, dataAfter);
        }

        custodyApprovalService.approveTemplateDJPCrsDeleteTinIndividual(DocRefID_AccountReport_Tab3);
        redirectAttr.addFlashAttribute("SuccessTinIndividualDelete","Success!, The deleted file with DocRefIdAccountReport "+DocRefID_AccountReport_Tab3+" has been approved.");

        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approval/crs#AccountReportTinIndividualDelete";
    }

    @GetMapping("approval/approvaltemplate/crsInOrganization/{DocRefID_AccountReport_Tab3}")
    public String appovalTemplateCrsInOrganization(@PathVariable("DocRefID_AccountReport_Tab3") String DocRefID_AccountReport_Tab3, HttpServletRequest pRequest, Model pModel,
                                                   RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve In Organization Edit Data CRS Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);
        int no = 4;

        CustodyCRSResultTTModel before = cRSCustodyResult_TTRepository.getAllDataTab3(DocRefID_AccountReport_Tab3);
        CRSImportCustodyInOrganizationModel after = importCustodyInOrganizationRepository.getAllData(DocRefID_AccountReport_Tab3);

        String dataBefore = "1. DocRefID : "+before.getDocRefID_AccountReport_Tab3()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        String dataAfter = "1. DocRefID : "+after.getDocRefID_AccountReport_Tab3()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        if(!before.getTIN().equals(after.getTIN_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". TIN : "+before.getTIN();
            dataAfter  = dataAfter + "\n "+no+". TIN : "+ after.getTIN_imp();
        }

        if(!before.getIssuedBy().equals(after.getIssuedBy_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". IssuedBy : "+before.getIssuedBy();
            dataAfter  = dataAfter + "\n "+no+". IssuedBy : "+ after.getIssuedBy_imp();
        }

        if(!before.getINType().equals(after.getINType_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". INType : "+before.getINType();
            dataAfter  = dataAfter + "\n "+no+". INType : "+ after.getINType_imp();
        }

        if (cookies != null) {
            auditTrailCustodyService.postAuditTrail(userLoginApp, Operation, AccessScreen, dataBefore, dataAfter);
        }

        custodyApprovalService.approveTemplateDJPCrsInOrganization(DocRefID_AccountReport_Tab3);
        redirectAttr.addFlashAttribute("SuccessinOrganization","Success!, The corrected file with DocRefIdAccountReport "+DocRefID_AccountReport_Tab3+" has been approved.");

        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approval/crs#AccountReportTinInOrganization";
    }


    @GetMapping("approval/approvaltemplate/crsDeleteInOrganization/{DocRefID_AccountReport_Tab3}")
    public String appovalTemplateCrsInOrganizationDelete(@PathVariable("DocRefID_AccountReport_Tab3") String DocRefID_AccountReport_Tab3, HttpServletRequest pRequest, Model pModel,
                                                   RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve Delete In Organization Edit Data CRS Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);
        int no = 4;


        CustodyCRSResultTTModel before = cRSCustodyResult_TTRepository.getAllDataTab3(DocRefID_AccountReport_Tab3);
        CRSImportDeleteCustodyInOrganizationModel after = importDeleteCustodyInOrganizationRepository.getAllData(DocRefID_AccountReport_Tab3);

        String dataBefore = "1. DocRefID : "+before.getDocRefID_AccountReport_Tab3()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        String dataAfter = "1. DocRefID : "+after.getDocRefID_AccountReport_Tab3()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        if(!before.getTIN().equals(after.getTIN_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". TIN : "+before.getTIN();
            dataAfter  = dataAfter + "\n "+no+". TIN : "+ after.getTIN_imp();
        }

        if(!before.getIssuedBy().equals(after.getIssuedBy_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". IssuedBy : "+before.getIssuedBy();
            dataAfter  = dataAfter + "\n "+no+". IssuedBy : "+ after.getIssuedBy_imp();
        }

        if(!before.getINType().equals(after.getINType_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". INType : "+before.getINType();
            dataAfter  = dataAfter + "\n "+no+". INType : "+ after.getINType_imp();
        }

        if (cookies != null) {
            auditTrailCustodyService.postAuditTrail(userLoginApp, Operation, AccessScreen, dataBefore, dataAfter);
        }

        custodyApprovalService.approveTemplateDJPCrsDeleteInOrganization(DocRefID_AccountReport_Tab3);
        redirectAttr.addFlashAttribute("SuccessinOrganizationDelete","Success!, The deleted file with DocRefIdAccountReport "+DocRefID_AccountReport_Tab3+" has been approved.");

        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approval/crs#AccountReportTinInOrganizationDelete";
    }

    @GetMapping("approval/approvaltemplate/crsNameIndividual/{DocRefID_AccountReport_Tab5}")
    public String appovalTemplateCrsNameIndividual(@PathVariable("DocRefID_AccountReport_Tab5") String DocRefID_AccountReport_Tab5, HttpServletRequest pRequest, Model pModel,
                                                   RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve Name Individual Edit Data CRS Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);
        int no = 4;

        CustodyCRSResultTTModel before = cRSCustodyResult_TTRepository.getAllDataTab5(DocRefID_AccountReport_Tab5);
        CRSImportCustodyNameIndividualModel after = importCustodyNameIndividualRepository.getAllData(DocRefID_AccountReport_Tab5);

        String dataBefore = "1. DocRefID : "+before.getDocRefID_AccountReport_Tab5()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        String dataAfter = "1. DocRefID : "+after.getDocRefID_AccountReport_Tab5()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        if(!ObjectUtils.isEmpty(before.getFirstName())) {
            if (!before.getFirstName().equals(after.getFirstName_imp())) {
                no++;
                dataBefore = dataBefore + "\n "+no+". FirstName : " + before.getFirstName();
                dataAfter = dataAfter + "\n "+no+". FirstName : " + after.getFirstName_imp();
            }
        }

        if(!ObjectUtils.isEmpty(before.getMiddleName())) {
            if (!before.getMiddleName().equals(after.getMiddleName_imp())) {
                no++;
                dataBefore = dataBefore + "\n "+no+". MiddleName : " + before.getMiddleName();
                dataAfter = dataAfter + "\n "+no+". MiddleName : " + after.getMiddleName_imp();
            }
        }

        if(!ObjectUtils.isEmpty(before.getLastName())) {
            if (!before.getLastName().equals(after.getLastName_imp())) {
                no++;
                dataBefore = dataBefore + "\n "+no+". LastName : " + before.getLastName();
                dataAfter = dataAfter + "\n "+no+". LastName : " + after.getLastName_imp();
            }
        }

        if(!before.getNameType().equals(after.getNameType_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". NameType : "+before.getNameType();
            dataAfter  = dataAfter + "\n "+no+". NameType : "+ after.getNameType_imp();
        }


        if (cookies != null) {
            auditTrailCustodyService.postAuditTrail(userLoginApp, Operation, AccessScreen, dataBefore, dataAfter);
        }

        custodyApprovalService.approveTemplateDJPCrsNameIndividual(DocRefID_AccountReport_Tab5);
        redirectAttr.addFlashAttribute("SuccessnameIndividual","Success!, The corrected file with DocRefIdAccountReport "+DocRefID_AccountReport_Tab5+" has been approved.");

        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approval/crs#AccountReportNameIndividual";
    }

    @GetMapping("approval/approvaltemplate/crsDeleteNameIndividual/{DocRefID_AccountReport_Tab5}")
    public String appovalTemplateCrsNameIndividualDelete(@PathVariable("DocRefID_AccountReport_Tab5") String DocRefID_AccountReport_Tab5, HttpServletRequest pRequest, Model pModel,
                                                   RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve Delete Name Individual Edit Data CRS Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);
        int no = 4;

        CustodyCRSResultTTModel before = cRSCustodyResult_TTRepository.getAllDataTab5(DocRefID_AccountReport_Tab5);
        CRSImportDeleteCustodyNameIndividualModel after = importDeleteCustodyNameIndividualRepository.getAllData(DocRefID_AccountReport_Tab5);

        String dataBefore = "1. DocRefID : "+before.getDocRefID_AccountReport_Tab5()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        String dataAfter = "1. DocRefID : "+after.getDocRefID_AccountReport_Tab5()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        if(!ObjectUtils.isEmpty(before.getFirstName())) {
            if (!before.getFirstName().equals(after.getFirstName_imp())) {
                no++;
                dataBefore = dataBefore + "\n "+no+". FirstName : " + before.getFirstName();
                dataAfter = dataAfter + "\n "+no+". FirstName : " + after.getFirstName_imp();
            }
        }

        if(!ObjectUtils.isEmpty(before.getMiddleName())) {
            if (!before.getMiddleName().equals(after.getMiddleName_imp())) {
                no++;
                dataBefore = dataBefore + "\n "+no+". MiddleName : " + before.getMiddleName();
                dataAfter = dataAfter + "\n "+no+". MiddleName : " + after.getMiddleName_imp();
            }
        }

        if(!ObjectUtils.isEmpty(before.getLastName())) {
            if (!before.getLastName().equals(after.getLastName_imp())) {
                no++;
                dataBefore = dataBefore + "\n "+no+". LastName : " + before.getLastName();
                dataAfter = dataAfter + "\n "+no+". LastName : " + after.getLastName_imp();
            }
        }

        if(!before.getNameType().equals(after.getNameType_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". NameType : "+before.getNameType();
            dataAfter  = dataAfter + "\n "+no+". NameType : "+ after.getNameType_imp();
        }


        if (cookies != null) {
            auditTrailCustodyService.postAuditTrail(userLoginApp, Operation, AccessScreen, dataBefore, dataAfter);
        }

        custodyApprovalService.approveTemplateDJPCrsDeleteNameIndividual(DocRefID_AccountReport_Tab5);
        redirectAttr.addFlashAttribute("SuccessnameIndividualDelete","Success!, The deleted file with DocRefIdAccountReport "+DocRefID_AccountReport_Tab5+" has been approved.");

        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approval/crs#AccountReportNameIndividualDelete";
    }

    @GetMapping("approval/approvaltemplate/crsNameOrganization/{DocRefID_AccountReport_Tab5}")
    public String appovalTemplateCrsNameOrganization(@PathVariable("DocRefID_AccountReport_Tab5") String DocRefID_AccountReport_Tab5, HttpServletRequest pRequest, Model pModel,
                                                     RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve Name Organization Edit Data CRS Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);
        int no = 4;

        CustodyCRSResultTTModel before = cRSCustodyResult_TTRepository.getAllDataTab5(DocRefID_AccountReport_Tab5);
        CRSImportCustodyNameOrganizationModel after = importCustodyNameOrganizationRepository.getAllData(DocRefID_AccountReport_Tab5);

        String dataBefore = "1. DocRefID : "+before.getDocRefID_AccountReport_Tab5()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        String dataAfter = "1. DocRefID : "+after.getDocRefID_AccountReport_Tab5()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "+after.getName_imp();
               // +(before.getName()==null?" ":before.getName());

//        if(!ObjectUtils.isEmpty(before.getName())) {
//            if (!before.getName().equals(after.getName_imp())) {
//                no++;
//                dataBefore = dataBefore + "\n "+no+". Name : " + before.getName();
//                dataAfter = dataAfter + "\n "+no+". Name : " + after.getName_imp();
//            }
//        }

        if(!before.getNameType().equals(after.getNameType_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". NameType : "+before.getNameType();
            dataAfter  = dataAfter + "\n "+no+". NameType : "+ after.getNameType_imp();
        }

        if (cookies != null) {
            auditTrailCustodyService.postAuditTrail(userLoginApp, Operation, AccessScreen, dataBefore, dataAfter);
        }

        custodyApprovalService.approveTemplateDJPCrsNameOrganization(DocRefID_AccountReport_Tab5);
        redirectAttr.addFlashAttribute("SuccessnameOrganization","Success!, The corrected file with DocRefIdAccountReport "+DocRefID_AccountReport_Tab5+" has been approved.");

        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approval/crs#AccountReportNameOrganization";
    }

    @GetMapping("approval/approvaltemplate/crsDeleteNameOrganization/{DocRefID_AccountReport_Tab5}")
    public String appovalTemplateCrsNameOrganizationDelete(@PathVariable("DocRefID_AccountReport_Tab5") String DocRefID_AccountReport_Tab5, HttpServletRequest pRequest, Model pModel,
                                                     RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve Delete Name Organization Edit Data CRS Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);
        int no =4;

        CustodyCRSResultTTModel before = cRSCustodyResult_TTRepository.getAllDataTab5(DocRefID_AccountReport_Tab5);
        CRSImportDeleteCustodyNameOrganizationModel after = importDeleteCustodyNameOrganizationRepository.getAllData(DocRefID_AccountReport_Tab5);
        String dataBefore = "1. DocRefID : "+before.getDocRefID_AccountReport_Tab5()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        String dataAfter = "1. DocRefID : "+after.getDocRefID_AccountReport_Tab5()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : " + after.getName();
               // +(before.getName()==null?" ":before.getName());

//        if(!ObjectUtils.isEmpty(before.getName())) {
//            if (!before.getName().equals(after.getName_imp())) {
//                no++;
//                dataBefore = dataBefore + "\n "+no+". Name : " + before.getName();
//                dataAfter = dataAfter + "\n "+no+". Name : " + after.getName_imp();
//            }
//        }

        if(!before.getNameType().equals(after.getNameType_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". NameType : "+before.getNameType();
            dataAfter  = dataAfter + "\n "+no+". NameType : "+ after.getNameType_imp();
        }

        if (cookies != null) {
            auditTrailCustodyService.postAuditTrail(userLoginApp, Operation, AccessScreen, dataBefore, dataAfter);
        }

        custodyApprovalService.approveTemplateDJPCrsDeleteNameOrganization(DocRefID_AccountReport_Tab5);
        redirectAttr.addFlashAttribute("SuccessnameOrganizationDelete","Success!, The deleted file with DocRefIdAccountReport "+DocRefID_AccountReport_Tab5+" has been approved.");

        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approval/crs#AccountReportNameOrganizationDelete";
    }

    @GetMapping("approval/approvaltemplate/crsRepAddress/{DocRefID_AccountReport_Tab6}")
    public String appovalTemplateCrsRepAddress(@PathVariable("DocRefID_AccountReport_Tab6") String DocRefID_AccountReport_Tab6, HttpServletRequest pRequest, Model pModel,
                                               RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve Report Address Edit Data CRS Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);
        int no = 4;

        CustodyCRSResultTTModel before = cRSCustodyResult_TTRepository.getAllDataTab6(DocRefID_AccountReport_Tab6);
        CRSImportCustodyRepAddressModel after = importCustodyRepAddressRepository.getAllData(DocRefID_AccountReport_Tab6);


        String dataBefore = "1. DocRefID : "+before.getDocRefID_AccountReport_Tab6()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        String dataAfter = "1. DocRefID : "+after.getDocRefID_AccountReport_Tab6()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        if(!before.getLegalAddressType().equals(after.getLegalAddressType_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". LegalAddressType : "+before.getLegalAddressType();
            dataAfter  = dataAfter + "\n "+no+". LegalAddressType : "+ after.getLegalAddressType_imp();
        }
        if(!before.getCountryCode().equals(after.getCountryCode_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". CountryCode : "+before.getCountryCode();
            dataAfter  = dataAfter + "\n "+no+". CountryCode : "+ after.getCountryCode_imp();
        }

        if(!before.getAlamatDomPemegangRek().equals(after.getAddressFree_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". AddressFree : "+before.getAlamatDomPemegangRek();
            dataAfter  = dataAfter + "\n "+no+". AddressFree : "+ after.getAddressFree_imp();
        }


        if (cookies != null) {
            auditTrailCustodyService.postAuditTrail(userLoginApp, Operation, AccessScreen, dataBefore, dataAfter);
        }

        custodyApprovalService.approveTemplateDJPCrsRepAddress(DocRefID_AccountReport_Tab6);
        redirectAttr.addFlashAttribute("SuccessrepAddress","Success!, The corrected file with DocRefIdAccountReport "+DocRefID_AccountReport_Tab6+" has been approved.");

        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approval/crs#AccountReportAddress";
    }


    @GetMapping("approval/approvaltemplate/crsDeleteRepAddress/{DocRefID_AccountReport_Tab6}")
    public String appovalTemplateCrsRepAddressDelete(@PathVariable("DocRefID_AccountReport_Tab6") String DocRefID_AccountReport_Tab6, HttpServletRequest pRequest, Model pModel,
                                               RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve Delete Report Address Edit Data CRS Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);
        int no =4;


        CustodyCRSResultTTModel before = cRSCustodyResult_TTRepository.getAllDataTab6(DocRefID_AccountReport_Tab6);
        CRSImportDeleteCustodyRepAddressModel after = importDeleteCustodyRepAddressRepository.getAllData(DocRefID_AccountReport_Tab6);

        String dataBefore = "1. DocRefID : "+before.getDocRefID_AccountReport_Tab6()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        String dataAfter = "1. DocRefID : "+after.getDocRefID_AccountReport_Tab6()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        if(!before.getLegalAddressType().equals(after.getLegalAddressType_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". LegalAddressType : "+before.getLegalAddressType();
            dataAfter  = dataAfter + "\n "+no+". LegalAddressType : "+ after.getLegalAddressType_imp();
        }
        if(!before.getCountryCode().equals(after.getCountryCode_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". CountryCode : "+before.getCountryCode();
            dataAfter  = dataAfter + "\n "+no+". CountryCode : "+ after.getCountryCode_imp();
        }

        if(!before.getAlamatDomPemegangRek().equals(after.getAddressFree_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". AddressFree : "+before.getAlamatDomPemegangRek();
            dataAfter  = dataAfter + "\n "+no+". AddressFree : "+ after.getAddressFree_imp();
        }


        if (cookies != null) {
            auditTrailCustodyService.postAuditTrail(userLoginApp, Operation, AccessScreen, dataBefore, dataAfter);
        }

        custodyApprovalService.approveTemplateDJPCrsDeleteRepAddress(DocRefID_AccountReport_Tab6);
        redirectAttr.addFlashAttribute("SuccessrepAddressDelete","Success!, The deleted file with DocRefIdAccountReport "+DocRefID_AccountReport_Tab6+" has been approved.");

        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approval/crs#DeleteAccountReportAddress";
    }

    @GetMapping("approval/approvaltemplate/crsRepPayment/{DocRefID_AccountReport_Tab7}")
    public String appovalTemplateCrsRepPayment(@PathVariable("DocRefID_AccountReport_Tab7") String DocRefID_AccountReport_Tab7, HttpServletRequest pRequest, Model pModel,
                                               RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve Report Payment Edit Data CRS Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);
        int no = 4;

        CustodyCRSResultTTModel before = cRSCustodyResult_TTRepository.getAllDataTab7(DocRefID_AccountReport_Tab7);
        CRSImportCustodyRepPaymentModel after = importCustodyRepPaymentRepository.getAllData(DocRefID_AccountReport_Tab7);

        String dataBefore = "1. DocRefID : "+before.getDocRefID_AccountReport_Tab7()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        String dataAfter = "1. DocRefID : "+after.getDocRefID_AccountReport_Tab7()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        if(!before.getPaymentType().equals(after.getPaymentType_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". PaymentType : "+before.getPaymentType();
            dataAfter  = dataAfter + "\n "+no+". PaymentType : "+ after.getPaymentType_imp();
        }
        if(!before.getCurrencyCodePayment().equals(after.getCurrencyCodePayment_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". CurrencyCodePayment : "+before.getCurrencyCodePayment();
            dataAfter  = dataAfter + "\n "+no+". CurrencyCodePayment : "+ after.getCurrencyCodePayment_imp();
        }

        if(!twoPlaces.format(before.getPaymentAmnt()).equals(twoPlaces.format(Double.valueOf(after.getPaymentAmnt_imp())))){
            no++;
            dataBefore = dataBefore + "\n "+no+". PaymentAmnt : "+twoPlaces.format(before.getPaymentAmnt());
            dataAfter  = dataAfter + "\n "+no+". PaymentAmnt : "+ twoPlaces.format(Double.valueOf(after.getPaymentAmnt_imp()));
        }

        if (cookies != null) {
            auditTrailCustodyService.postAuditTrail(userLoginApp, Operation, AccessScreen, dataBefore, dataAfter);

        }

        custodyApprovalService.approveTemplateDJPCrsRepPayment(DocRefID_AccountReport_Tab7);
        redirectAttr.addFlashAttribute("SuccessrepPayment","Success!, The corrected file with DocRefIdAccountReport "+DocRefID_AccountReport_Tab7+" has been approved.");

        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approval/crs#AccountReportPayment";
    }


    @GetMapping("approval/approvaltemplate/crsDeleteRepPayment/{DocRefID_AccountReport_Tab7}")
    public String appovalTemplateCrsRepPaymentDelete(@PathVariable("DocRefID_AccountReport_Tab7") String DocRefID_AccountReport_Tab7, HttpServletRequest pRequest, Model pModel,
                                               RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve Delete Report Payment Edit Data CRS Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);
        int no = 4;

        CustodyCRSResultTTModel before = cRSCustodyResult_TTRepository.getAllDataTab7(DocRefID_AccountReport_Tab7);
        CRSImportDeleteCustodyRepPaymentModel after = importDeleteCustodyRepPaymentRepository.getAllData(DocRefID_AccountReport_Tab7);

        String dataBefore = "1. DocRefID : "+before.getDocRefID_AccountReport_Tab7()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        String dataAfter = "1. DocRefID : "+after.getDocRefID_AccountReport_Tab7()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        if(!before.getPaymentType().equals(after.getPaymentType_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". PaymentType : "+before.getPaymentType();
            dataAfter  = dataAfter + "\n "+no+". PaymentType : "+ after.getPaymentType_imp();
        }
        if(!before.getCurrencyCodePayment().equals(after.getCurrencyCodePayment_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". CurrencyCodePayment : "+before.getCurrencyCodePayment();
            dataAfter  = dataAfter + "\n "+no+". CurrencyCodePayment : "+ after.getCurrencyCodePayment_imp();
        }

        if(!twoPlaces.format(before.getPaymentAmnt()).equals(after.getPaymentAmnt_imp())){
            no++;
            dataBefore = dataBefore + "\n "+no+". PaymentAmnt : "+before.getPaymentAmnt();
            dataAfter  = dataAfter + "\n "+no+". PaymentAmnt : "+ after.getPaymentAmnt_imp();
        }

        if (cookies != null) {
            auditTrailCustodyService.postAuditTrail(userLoginApp, Operation, AccessScreen, dataBefore, dataAfter);

        }

        custodyApprovalService.approveTemplateDJPCrsDeleteRepPayment(DocRefID_AccountReport_Tab7);
        redirectAttr.addFlashAttribute("SuccessrepPaymentDelete","Success!, The deleted file with DocRefIdAccountReport "+DocRefID_AccountReport_Tab7+" has been approved.");

        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approval/crs#DeleteAccountReportPayment";
    }

    @GetMapping("approval/approvaldjp/crsAccountReport/{DocRefID_AccountReport_Tab1}")
    public String appovalDjpCrs(@PathVariable("DocRefID_AccountReport_Tab1") String DocRefID_AccountReport_Tab1, HttpServletRequest pRequest, Model pModel,
                                     RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve Account Report Koreksi Pelaporan CRS Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);
        int no = 4;

        CustodyCRSResultTTModel before = cRSCustodyResult_TTRepository.getAllData(DocRefID_AccountReport_Tab1);
        CRSImportCustodyDjpModel after = importCustodyAccountReportDjpRepository.getAllData(DocRefID_AccountReport_Tab1);
        CRSORRGetFinalDataAccountReport cRSORRGetFinalDataAccountReport = custodyApprovalService.approveKoreksiDJPCrsAccountReport(DocRefID_AccountReport_Tab1);

        String dataBefore = "1. DocRefID : "+before.getDocRefID_AccountReport_Tab7()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        String dataAfter = "1. DocRefID : "+cRSORRGetFinalDataAccountReport.getListCRSAccountReportModel().listIterator().next().getDocRefIDAccountReport()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+after.getAccountNumber_imp()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());


        if(!before.getUndocumentedAccount().equals(cRSORRGetFinalDataAccountReport.getListCRSAccountReportModel().listIterator().next().getUndocumentedAccount())){
            no++;
            dataBefore = dataBefore + "\n "+no+". UndocumentedAccount : "+ before.getUndocumentedAccount();
            dataAfter  = dataAfter + "\n "+no+". UndocumentedAccount : "+ cRSORRGetFinalDataAccountReport.getListCRSAccountReportModel().listIterator().next().getUndocumentedAccount();
        }

        if(!before.getClosedAccount().equals(cRSORRGetFinalDataAccountReport.getListCRSAccountReportModel().listIterator().next().getClosedAccount())){
            no++;
            dataBefore = dataBefore + "\n "+no+". ClosedAccount : "+before.getClosedAccount();
            dataAfter  = dataAfter + "\n "+no+". ClosedAccount : "+ cRSORRGetFinalDataAccountReport.getListCRSAccountReportModel().listIterator().next().getClosedAccount();
        }
        if(!before.getDormantAccount().equals(cRSORRGetFinalDataAccountReport.getListCRSAccountReportModel().listIterator().next().getDormantAccount())){
            no++;
            dataBefore = dataBefore + "\n "+no+". DormantAccount : "+before.getDormantAccount();
            dataAfter  = dataAfter + "\n "+no+". DormantAccount : "+ cRSORRGetFinalDataAccountReport.getListCRSAccountReportModel().listIterator().next().getDormantAccount();
        }

        if(!before.getIsIndividual().equals(cRSORRGetFinalDataAccountReport.getListCRSAccountReportModel().listIterator().next().getIsIndividual())){
            no++;
            dataBefore = dataBefore + "\n "+no+". IsIndividual : "+before.getIsIndividual();
            dataAfter  = dataAfter + "\n "+no+". IsIndividual : "+ cRSORRGetFinalDataAccountReport.getListCRSAccountReportModel().listIterator().next().getIsIndividual();
        }

        if(!before.getAcctHolderType().equals(cRSORRGetFinalDataAccountReport.getListCRSAccountReportModel().listIterator().next().getAcctHolderType())){
            no++;
            dataBefore = dataBefore + "\n "+no+". AcctHolderType : "+before.getAcctHolderType();
            dataAfter  = dataAfter + "\n "+no+". AcctHolderType : "+ cRSORRGetFinalDataAccountReport.getListCRSAccountReportModel().listIterator().next().getAcctHolderType();
        }

        if(!before.getResCountryCode().equals(cRSORRGetFinalDataAccountReport.getListCRSAccountReportModel().listIterator().next().getResCountryCode())){
            no++;
            dataBefore = dataBefore + "\n "+no+". ResCountryCode : "+before.getResCountryCode();
            dataAfter  = dataAfter + "\n "+no+". ResCountryCode : "+ cRSORRGetFinalDataAccountReport.getListCRSAccountReportModel().listIterator().next().getResCountryCode();
        }

        if(!before.getCurrencyCodeAccountBalance().equals(cRSORRGetFinalDataAccountReport.getListCRSAccountReportModel().listIterator().next().getCurrencyCodeAccountBalance())){
            no++;
            dataBefore = dataBefore + "\n "+no+". CurrencyCodeAccountBalance : "+before.getCurrencyCodeAccountBalance();
            dataAfter  = dataAfter + "\n "+no+". CurrencyCodeAccountBalance : "+ cRSORRGetFinalDataAccountReport.getListCRSAccountReportModel().listIterator().next().getCurrencyCodeAccountBalance();
        }

        if(!twoPlaces.format(before.getAccountBalance()).equals(cRSORRGetFinalDataAccountReport.getListCRSAccountReportModel().listIterator().next().getAccountBalance())){
            no++;
            dataBefore = dataBefore + "\n "+no+". AccountBalance : "+ before.getAccountBalance();
            dataAfter  = dataAfter + "\n "+no+". AccountBalance : "+ cRSORRGetFinalDataAccountReport.getListCRSAccountReportModel().listIterator().next().getAccountBalance();
        }

        if(!before.getBirthInfo_BirthDate().equals(cRSORRGetFinalDataAccountReport.getListCRSAccountReportModel().listIterator().next().getBirthDate())){
            no++;
            dataBefore = dataBefore + "\n "+no+". BirthInfo_BirthDate : "+before.getBirthInfo_BirthDate();
            dataAfter  = dataAfter + "\n "+no+". BirthInfo_BirthDate : "+ cRSORRGetFinalDataAccountReport.getListCRSAccountReportModel().listIterator().next().getBirthDate();
        }

        if (cookies != null) {
            auditTrailCustodyService.postAuditTrail(userLoginApp, Operation, AccessScreen,dataBefore, dataAfter);
        }

        redirectAttr.addFlashAttribute("Success","Success!, The corrected file with DocRefIdAccountReport "+DocRefID_AccountReport_Tab1+" has been approved.");

        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approvaldjp/crs";
    }

    @GetMapping("approval/approvaldjp/crsTinIndividual/{DocRefID_AccountReport_Tab3}")
    public String appovalTemplateCrsTinIndividualDjp(@PathVariable("DocRefID_AccountReport_Tab3") String DocRefID_AccountReport_Tab3, HttpServletRequest pRequest, Model pModel,
                                                  RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve TIN Individual Koreksi Pelaporan CRS Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);
        int no = 4;

        CRSORRGetFinalDataAccountReport cRSORRGetFinalDataAccountReport = custodyApprovalService.approveKoreksiDJPCrsTinIndividual(DocRefID_AccountReport_Tab3);
        CustodyCRSResultTTModel before = cRSCustodyResult_TTRepository.getAllDataTab3(DocRefID_AccountReport_Tab3);

        String dataBefore = "1. DocRefID : "+before.getDocRefID_AccountReport_Tab7()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        String dataAfter = "1. DocRefID : "+cRSORRGetFinalDataAccountReport.getListCRSAccountReportTinIndividualModel().listIterator().next().getDocRefID_AccountReport()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        if(!before.getTIN().equals(cRSORRGetFinalDataAccountReport.getListCRSAccountReportTinIndividualModel().listIterator().next().getIN())){
            no++;
            dataBefore = dataBefore + "\n "+no+". TIN : "+before.getTIN();
            dataAfter  = dataAfter + "\n "+no+". TIN : "+ cRSORRGetFinalDataAccountReport.getListCRSAccountReportTinIndividualModel().listIterator().next().getIN();
        }

        if(!before.getIssuedBy().equals(cRSORRGetFinalDataAccountReport.getListCRSAccountReportTinIndividualModel().listIterator().next().getIssuedBy())){
            no++;
            dataBefore = dataBefore + "\n "+no+". IssuedBy : "+ before.getIssuedBy();
            dataAfter  = dataAfter + "\n "+no+". IssuedBy : "+ cRSORRGetFinalDataAccountReport.getListCRSAccountReportTinIndividualModel().listIterator().next().getIssuedBy();
        }

        if (cookies != null) {
            auditTrailCustodyService.postAuditTrail(userLoginApp, Operation, AccessScreen, dataBefore, dataAfter);
        }

        redirectAttr.addFlashAttribute("SuccessTinIndividual","Success!, The corrected file with DocRefIdAccountReport "+DocRefID_AccountReport_Tab3+" has been approved.");

        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approvaldjp/crs#AccountReportTinIndividual";
    }

    @GetMapping("approval/approvaldjp/crsInOrganization/{DocRefID_AccountReport_Tab3}")
    public String appovalTemplateCrsInOrganizationDjp(@PathVariable("DocRefID_AccountReport_Tab3") String DocRefID_AccountReport_Tab3, HttpServletRequest pRequest, Model pModel,
                                                   RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve In Organization Koreksi Pelaporan CRS Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);
        int no = 4;

        CRSORRGetFinalDataAccountReport cRSORRGetFinalDataAccountReport =  custodyApprovalService.approveKoreksiDJPCrsInOrganization(DocRefID_AccountReport_Tab3);
        CustodyCRSResultTTModel before = cRSCustodyResult_TTRepository.getAllDataTab3(DocRefID_AccountReport_Tab3);

        String dataBefore = "1. DocRefID : "+before.getDocRefID_AccountReport_Tab7()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        String dataAfter = "1. DocRefID : "+cRSORRGetFinalDataAccountReport.getListCRSAccountReportInOrganisationModel().listIterator().next().getDocRefID_AccountReport()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        if(!before.getTIN().equals(cRSORRGetFinalDataAccountReport.getListCRSAccountReportInOrganisationModel().listIterator().next().getIN())){
            no++;
            dataBefore = dataBefore + "\n "+no+". TIN : "+before.getTIN();
            dataAfter  = dataAfter + "\n "+no+". TIN : "+ cRSORRGetFinalDataAccountReport.getListCRSAccountReportInOrganisationModel().listIterator().next().getIN();
        }

        if(!before.getIssuedBy().equals(cRSORRGetFinalDataAccountReport.getListCRSAccountReportInOrganisationModel().listIterator().next().getIssuedBy())){
            no++;
            dataBefore = dataBefore + "\n "+no+". IssuedBy : "+before.getIssuedBy();
            dataAfter  = dataAfter + "\n "+no+". IssuedBy : "+ cRSORRGetFinalDataAccountReport.getListCRSAccountReportInOrganisationModel().listIterator().next().getIssuedBy();
        }

        if(!before.getINType().equals(cRSORRGetFinalDataAccountReport.getListCRSAccountReportInOrganisationModel().listIterator().next().getINType())){
            no++;
            dataBefore = dataBefore + "\n "+no+". INType : "+before.getINType();
            dataAfter  = dataAfter + "\n "+no+". INType : "+ cRSORRGetFinalDataAccountReport.getListCRSAccountReportInOrganisationModel().listIterator().next().getINType();
        }

        if (cookies != null) {
            auditTrailCustodyService.postAuditTrail(userLoginApp, Operation, AccessScreen, dataBefore, dataAfter);
        }

        redirectAttr.addFlashAttribute("SuccessinOrganization","Success!, The corrected file with DocRefIdAccountReport "+DocRefID_AccountReport_Tab3+" has been approved.");

        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approvaldjp/crs#AccountReportTinInOrganization";
    }

    @GetMapping("approval/approvaldjp/crsNameIndividual/{DocRefID_AccountReport_Tab5}")
    public String appovalTemplateCrsNameIndividualDjp(@PathVariable("DocRefID_AccountReport_Tab5") String DocRefID_AccountReport_Tab5, HttpServletRequest pRequest, Model pModel,
                                                   RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve Name Individual Koreksi Pelaporan CRS Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);
        int no = 4;

        CRSORRGetFinalDataAccountReport cRSORRGetFinalDataAccountReport =  custodyApprovalService.approveKoreksiDJPCrsNameIndividual(DocRefID_AccountReport_Tab5);
        CustodyCRSResultTTModel before = cRSCustodyResult_TTRepository.getAllDataTab5(DocRefID_AccountReport_Tab5);

        String dataBefore = "1. DocRefID : "+before.getDocRefID_AccountReport_Tab7()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() :before.getName());

        String dataAfter = "1. DocRefID : "+cRSORRGetFinalDataAccountReport.getListCRSAccountReportNameIndividualModel().listIterator().next().getDocRefID_AccountReport()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() :before.getName());

        if(!ObjectUtils.isEmpty(before.getFirstName())) {
            if (!before.getFirstName().equals(cRSORRGetFinalDataAccountReport.getListCRSAccountReportNameIndividualModel().listIterator().next().getFirstName())) {
                no++;
                dataBefore = dataBefore + "\n "+no+". FirstName : " + before.getFirstName();
                dataAfter = dataAfter + "\n "+no+". FirstName : " + cRSORRGetFinalDataAccountReport.getListCRSAccountReportNameIndividualModel().listIterator().next().getFirstName();
            }
        }

        if(!ObjectUtils.isEmpty(before.getMiddleName())) {
            if (!before.getMiddleName().equals(cRSORRGetFinalDataAccountReport.getListCRSAccountReportNameIndividualModel().listIterator().next().getMiddleName())) {
                no++;
                dataBefore = dataBefore + "\n "+no+". MiddleName : " + before.getMiddleName();
                dataAfter = dataAfter + "\n "+no+". MiddleName : " + cRSORRGetFinalDataAccountReport.getListCRSAccountReportNameIndividualModel().listIterator().next().getMiddleName();
            }
        }

        if(!ObjectUtils.isEmpty(before.getLastName())) {
            if (!before.getLastName().equals(cRSORRGetFinalDataAccountReport.getListCRSAccountReportNameIndividualModel().listIterator().next().getLastName())) {
                no++;
                dataBefore = dataBefore + "\n "+no+". LastName : " + before.getLastName();
                dataAfter = dataAfter + "\n "+no+". LastName : " + cRSORRGetFinalDataAccountReport.getListCRSAccountReportNameIndividualModel().listIterator().next().getLastName();
            }
        }

        if(!before.getNameType().equals(cRSORRGetFinalDataAccountReport.getListCRSAccountReportNameIndividualModel().listIterator().next().getNameType())){
            no++;
            dataBefore = dataBefore + "\n "+no+". NameType : "+before.getNameType();
            dataAfter  = dataAfter + "\n "+no+". NameType : "+ cRSORRGetFinalDataAccountReport.getListCRSAccountReportNameIndividualModel().listIterator().next().getNameType();
        }

        if (cookies != null) {
            auditTrailCustodyService.postAuditTrail(userLoginApp, Operation, AccessScreen, dataBefore, dataAfter);
        }

        redirectAttr.addFlashAttribute("SuccessnameIndividual","Success!, The corrected file with DocRefIdAccountReport "+DocRefID_AccountReport_Tab5+" has been approved.");

        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approvaldjp/crs#AccountReportNameIndividual";
    }

    @GetMapping("approval/approvaldjp/crsNameOrganization/{DocRefID_AccountReport_Tab5}")
    public String appovalTemplateCrsNameOrganizationDjp(@PathVariable("DocRefID_AccountReport_Tab5") String DocRefID_AccountReport_Tab5, HttpServletRequest pRequest, Model pModel,
                                                     RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve Name Organization Koreksi Pelaporan CRS Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);
        int no = 4;

        CRSORRGetFinalDataAccountReport cRSORRGetFinalDataAccountReport =  custodyApprovalService.approveKoreksiDJPCrsNameOrganization(DocRefID_AccountReport_Tab5);
        CustodyCRSResultTTModel before = cRSCustodyResult_TTRepository.getAllDataTab5(DocRefID_AccountReport_Tab5);

        String dataBefore = "1. DocRefID : "+before.getDocRefID_AccountReport_Tab7()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() :before.getName());

        String dataAfter = "1. DocRefID : "+cRSORRGetFinalDataAccountReport.getListCRSAccountReportNameOrganisationModel().listIterator().next().getDocRefID_AccountReport()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "+cRSORRGetFinalDataAccountReport.getListCRSAccountReportNameOrganisationModel().listIterator().next().getName();
               // +(before.getName()==null?" ":before.getName());

//        if(!ObjectUtils.isEmpty(before.getName())) {
//            if (!before.getName().equals(cRSORRGetFinalDataAccountReport.getListCRSAccountReportNameOrganisationModel().iterator().next().getName())) {
//                no++;
//                dataBefore = dataBefore + "\n "+no+". Name : " + before.getName();
//                dataAfter = dataAfter + "\n "+no+". Name : " + cRSORRGetFinalDataAccountReport.getListCRSAccountReportNameOrganisationModel().iterator().next().getName();
//            }
//        }

        if(!before.getNameType().equals(cRSORRGetFinalDataAccountReport.getListCRSAccountReportNameOrganisationModel().iterator().next().getNameType())){
            no++;
            dataBefore = dataBefore + "\n "+no+". NameType : "+before.getNameType();
            dataAfter  = dataAfter + "\n "+no+". NameType : "+ cRSORRGetFinalDataAccountReport.getListCRSAccountReportNameOrganisationModel().iterator().next().getNameType();
        }


        if (cookies != null) {
            auditTrailCustodyService.postAuditTrail(userLoginApp, Operation, AccessScreen, dataBefore, dataAfter);
        }

        redirectAttr.addFlashAttribute("SuccessnameOrganization","Success!, The corrected file with DocRefIdAccountReport "+DocRefID_AccountReport_Tab5+" has been approved.");

        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approvaldjp/crs#AccountReportNameOrganization";
    }

    @GetMapping("approval/approvaldjp/crsRepAddress/{DocRefID_AccountReport_Tab6}")
    public String appovalTemplateCrsRepAddressDjp(@PathVariable("DocRefID_AccountReport_Tab6") String DocRefID_AccountReport_Tab6, HttpServletRequest pRequest, Model pModel,
                                               RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve Report Address Koreksi Pelaporan CRS Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);
        int no = 4;

        CRSORRGetFinalDataAccountReport cRSORRGetFinalDataAccountReport =  custodyApprovalService.approveKoreksiDJPCrsRepAddress(DocRefID_AccountReport_Tab6);
        CustodyCRSResultTTModel before = cRSCustodyResult_TTRepository.getAllDataTab6(DocRefID_AccountReport_Tab6);
        String dataBefore = "1. DocRefID : "+before.getDocRefID_AccountReport_Tab7()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() :before.getName());

        String dataAfter = "1. DocRefID : "+cRSORRGetFinalDataAccountReport.getListCRSAccountReportAddressModel().listIterator().next().getDocRefID_AccountReport()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() :before.getName());

        if(!before.getLegalAddressType().equals(cRSORRGetFinalDataAccountReport.getListCRSAccountReportAddressModel().iterator().next().getLegalAddressType())){
            no++;
            dataBefore = dataBefore + "\n "+no+". LegalAddressType : "+before.getLegalAddressType();
            dataAfter  = dataAfter + "\n "+no+". LegalAddressType : "+ cRSORRGetFinalDataAccountReport.getListCRSAccountReportAddressModel().iterator().next().getLegalAddressType();
        }
        if(!before.getCountryCode().equals(cRSORRGetFinalDataAccountReport.getListCRSAccountReportAddressModel().iterator().next().getCountryCode())){
            no++;
            dataBefore = dataBefore + "\n "+no+". CountryCode : "+before.getCountryCode();
            dataAfter  = dataAfter + "\n "+no+". CountryCode : "+ cRSORRGetFinalDataAccountReport.getListCRSAccountReportAddressModel().iterator().next().getCountryCode();
        }
        if(!before.getAlamatDomPemegangRek().equals(cRSORRGetFinalDataAccountReport.getListCRSAccountReportAddressModel().iterator().next().getAddressFree())){
            no++;
            dataBefore = dataBefore + "\n "+no+". AddressFree : "+before.getAlamatDomPemegangRek();
            dataAfter  = dataAfter + "\n "+no+". AddressFree : "+ cRSORRGetFinalDataAccountReport.getListCRSAccountReportAddressModel().iterator().next().getAddressFree();
        }

        if (cookies != null) {
            auditTrailCustodyService.postAuditTrail(userLoginApp, Operation, AccessScreen, dataBefore, dataAfter);
        }
        redirectAttr.addFlashAttribute("SuccessrepAddress","Success!, The corrected file with DocRefIdAccountReport "+DocRefID_AccountReport_Tab6+" has been approved.");

        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approvaldjp/crs#AccountReportAddress";
    }

    @GetMapping("approval/approvaldjp/crsRepPayment/{DocRefID_AccountReport_Tab7}")
    public String appovalTemplateCrsRepPaymentDjp(@PathVariable("DocRefID_AccountReport_Tab7") String DocRefID_AccountReport_Tab7, HttpServletRequest pRequest, Model pModel,
                                               RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve Report Payment Koreksi Pelaporan CRS Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);
        int no = 4;

        CRSORRGetFinalDataAccountReport cRSORRGetFinalDataAccountReport = custodyApprovalService.approveKoreksiDJPCrsRepPayment(DocRefID_AccountReport_Tab7);
        CustodyCRSResultTTModel before = cRSCustodyResult_TTRepository.getAllDataTab7(DocRefID_AccountReport_Tab7);

        String dataBefore = "1. DocRefID : "+before.getDocRefID_AccountReport_Tab7()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() : before.getName());

        String dataAfter = "1. DocRefID : "+cRSORRGetFinalDataAccountReport.getListCRSAccountReportPaymentModel().listIterator().next().getDocRefID_AccountReport()+"\n 2. CIF : "
                +before.getCIF()+"\n 3. AccountNumber : "+before.getAccountNumber()+"\n 4. Name : "
                + (before.getName().isEmpty() ? before.getFirstName()+" "+before.getMiddleName()+" "+before.getLastName() :before.getName());

        if(!before.getPaymentType().equals(cRSORRGetFinalDataAccountReport.getListCRSAccountReportPaymentModel().listIterator().next().getPaymentType())){
            no++;
            dataBefore = dataBefore + "\n "+no+". PaymentType : "+before.getPaymentType();
            dataAfter  = dataAfter + "\n "+no+". PaymentType : "+ cRSORRGetFinalDataAccountReport.getListCRSAccountReportPaymentModel().listIterator().next().getPaymentType();
        }
        if(!before.getCurrencyCodePayment().equals(cRSORRGetFinalDataAccountReport.getListCRSAccountReportPaymentModel().listIterator().next().getCurrencyCodePayment())){
            no++;
            dataBefore = dataBefore + "\n "+no+". CurrencyCodePayment : "+before.getCurrencyCodePayment();
            dataAfter  = dataAfter + "\n "+no+". CurrencyCodePayment : "+ cRSORRGetFinalDataAccountReport.getListCRSAccountReportPaymentModel().listIterator().next().getCurrencyCodePayment();
        }

        if(!twoPlaces.format(before.getPaymentAmnt()).equals(cRSORRGetFinalDataAccountReport.getListCRSAccountReportPaymentModel().listIterator().next().getPaymentAmnt())){
            no++;
            dataBefore = dataBefore + "\n "+no+". PaymentAmnt : "+twoPlaces.format(before.getPaymentAmnt());
            dataAfter  = dataAfter + "\n "+no+". PaymentAmnt : "+ cRSORRGetFinalDataAccountReport.getListCRSAccountReportPaymentModel().listIterator().next().getPaymentAmnt();
        }

        if (cookies != null) {
            auditTrailCustodyService.postAuditTrail(userLoginApp, Operation, AccessScreen, dataBefore, dataAfter);
        }

        redirectAttr.addFlashAttribute("SuccessrepPayment","Success!, The corrected file with DocRefIdAccountReport "+DocRefID_AccountReport_Tab7+" has been approved.");

        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approvaldjp/crs#AccountReportPayment";
    }

    @GetMapping("approval/approvaldataupload/{tahun}")
    public String appovalDataUpload(@PathVariable("tahun") String tahun,
                                    HttpServletRequest pRequest, Model pModel,
                                               RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve Upload Data Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);


        if (cookies != null) {
            auditTrailCustodyService.postAuditTrail(userLoginApp, Operation, AccessScreen,"","");
        }

        int approve = importCustodyUploadDataRepository.getApproveUploadData();
       // String tahun="";

        custodyApprovalService.approveDataUpload(tahun);

        redirectAttr.addFlashAttribute("SuccessDataUpload","Success!, The Data Upload has been approved.");

        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approval/data";
    }

    @GetMapping("approval/approvaldataupload/crs/{tahun}")
    public String appovalDataUploadCrs(@PathVariable("tahun") String tahun,
                                    HttpServletRequest pRequest, Model pModel,
                                    RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve Upload Data CRS Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);


        if (cookies != null) {
            auditTrailCustodyService.postAuditTrail(userLoginApp, Operation, AccessScreen,"","");
        }

        int approve = importCustodyUploadDataRepository.getApproveUploadData();
        // String tahun="";

        custodyApprovalService.approveDataUpload(tahun);

        redirectAttr.addFlashAttribute("SuccessDataUpload","Success!, The Data Upload has been approved.");

        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approval/data/crs";
    }

//    @GetMapping("approval/delete/crs")
//    public String getApprovalCrsDelete(RoleModel pRoleModel, HttpServletRequest pRequest, Model pModel)
//            throws IOException, JAXBException, ParseException {
//        log.info("Request Client : " + pRequest.getRemoteAddr());
//        log.info("Request URL : " + pRequest.getRequestURL());
//        log.info("Request Method : " + pRequest.getMethod());
//
//        Cookie[] cookies        = pRequest.getCookies();
//        String userLoginApp = Arrays.stream(cookies)
//                .filter(a->a.getName().equals("username"))
//                .map(c -> c.getValue()).collect(Collectors.joining(", "));
//        String Operation        = "View";
//        String AccessScreen     = "View Delete Approval Edit Data CRS Custody";
//        String UserIP           = HttpUtils.getRequestIP(pRequest);
//
//
//        if (cookies != null) {
//            log.info("Cookies List : " + Arrays.stream(cookies)
//                    .map(c -> c.getName() + "=" + c.getValue()).collect(Collectors.joining(", ")));
//
//            //Account Report
//            List<CRSImportDeleteCustodyModel> crsImportModel = new ArrayList<>();
//            crsImportModel = importDeleteCustodyAccountReportRepository.getAllApproval();
//            if (crsImportModel.isEmpty()) {
//                pModel.addAttribute("alreadyData", false);
//            } else {
//                pModel.addAttribute("alreadyData", true);
//                pModel.addAttribute("totalRoleData", crsImportModel.size());
//                pModel.addAttribute("userRoleData", crsImportModel);
//            }
//
//            //TIN Individual
//            List<CRSImportDeleteCustodyTinIndividualModel> tINIndividual = new ArrayList<>();
//            tINIndividual = importDeleteCustodyTinIndividualRepository.getAllApproval();
//            if (tINIndividual.isEmpty()) {
//                pModel.addAttribute("alreadyDataTinIndividual", false);
//            } else {
//                pModel.addAttribute("alreadyDataTinIndividual", true);
//                pModel.addAttribute("totalRoleDataTinIndividual", tINIndividual.size());
//                pModel.addAttribute("userRoleDataTinIndividual", tINIndividual);
//            }
//
//            //In Organization
//            List<CRSImportDeleteCustodyInOrganizationModel> inOrganization = new ArrayList<>();
//            inOrganization = importDeleteCustodyInOrganizationRepository.getAllApproval();
//            if (inOrganization.isEmpty()) {
//                pModel.addAttribute("alreadyDatainOrganization", false);
//            } else {
//                pModel.addAttribute("alreadyDatainOrganization", true);
//                pModel.addAttribute("totalRoleDatainOrganization", inOrganization.size());
//                pModel.addAttribute("userRoleDatainOrganization", inOrganization);
//            }
//
//            //Name Individual
//            List<CRSImportDeleteCustodyNameIndividualModel> nameIndividual = new ArrayList<>();
//            nameIndividual = importDeleteCustodyNameIndividualRepository.getAllApproval();
//            if (nameIndividual.isEmpty()) {
//                pModel.addAttribute("alreadyDatanameIndividual", false);
//            } else {
//                pModel.addAttribute("alreadyDatanameIndividual", true);
//                pModel.addAttribute("totalRoleDatanameIndividual", nameIndividual.size());
//                pModel.addAttribute("userRoleDatanameIndividual", nameIndividual);
//            }
//
//            //Name Organization
//            List<CRSImportDeleteCustodyNameOrganizationModel> nameOrganization = new ArrayList<>();
//            nameOrganization = importDeleteCustodyNameOrganizationRepository.getAllApproval();
//            if (nameOrganization.isEmpty()) {
//                pModel.addAttribute("alreadyDatanameOrganization", false);
//            } else {
//                pModel.addAttribute("alreadyDatanameOrganization", true);
//                pModel.addAttribute("totalRoleDatanameOrganization", nameOrganization.size());
//                pModel.addAttribute("userRoleDatanameOrganization", nameOrganization);
//            }
//
//            //Rep Address
//            List<CRSImportDeleteCustodyRepAddressModel> repAddress = new ArrayList<>();
//            repAddress = importDeleteCustodyRepAddressRepository.getAllApproval();
//            if (repAddress.isEmpty()) {
//                pModel.addAttribute("alreadyDatarepAddress", false);
//            } else {
//                pModel.addAttribute("alreadyDatarepAddress", true);
//                pModel.addAttribute("totalRoleDatarepAddress", repAddress.size());
//                pModel.addAttribute("userRoleDatarepAddress", repAddress);
//            }
//
//            //Rep Payment
//            List<CRSImportDeleteCustodyRepPaymentModel> repPayment = new ArrayList<>();
//            repPayment = importDeleteCustodyRepPaymentRepository.getAllApproval();
//            if (repPayment.isEmpty()) {
//                pModel.addAttribute("alreadyDatarepPayment", false);
//            } else {
//                pModel.addAttribute("alreadyDatarepPayment", true);
//                pModel.addAttribute("totalRoleDatarepPayment", repPayment.size());
//                pModel.addAttribute("userRoleDatarepPayment", repPayment);
//            }
//
//
//            // Delete Account Report
//            List<CRSImportDeleteCustodyModel> crsDeleteImportModel = new ArrayList<>();
//            crsDeleteImportModel = importDeleteCustodyAccountReportRepository.getAllApproval();
//            if (crsDeleteImportModel.isEmpty()) {
//                pModel.addAttribute("alreadyDataDeleteAccountReport", false);
//            } else {
//                pModel.addAttribute("alreadyDataDeleteAccountReport", true);
//                pModel.addAttribute("totalRoleDataDeleteAccountReport", crsDeleteImportModel.size());
//                pModel.addAttribute("userRoleDataDeleteAccountReport", crsDeleteImportModel);
//            }
//
//            //Delete TIN Individual
//            List<CRSImportDeleteCustodyTinIndividualModel> tINIndividualDelete = new ArrayList<>();
//            tINIndividualDelete = importDeleteCustodyTinIndividualRepository.getAllApproval();
//            if (tINIndividualDelete.isEmpty()) {
//                pModel.addAttribute("alreadyDataTinIndividualDelete", false);
//            } else {
//                pModel.addAttribute("alreadyDataTinIndividualDelete", true);
//                pModel.addAttribute("totalRoleDataTinIndividualDelete", tINIndividualDelete.size());
//                pModel.addAttribute("userRoleDataTinIndividualDelete", tINIndividualDelete);
//            }
//
//            //Delete In Organization
//            List<CRSImportDeleteCustodyInOrganizationModel> inOrganizationDelete = new ArrayList<>();
//            inOrganizationDelete = importDeleteCustodyInOrganizationRepository.getAllApproval();
//            if (inOrganizationDelete.isEmpty()) {
//                pModel.addAttribute("alreadyDatainOrganizationDelete", false);
//            } else {
//                pModel.addAttribute("alreadyDatainOrganizationDelete", true);
//                pModel.addAttribute("totalRoleDatainOrganizationDelete", inOrganizationDelete.size());
//                pModel.addAttribute("userRoleDatainOrganizationDelete", inOrganizationDelete);
//            }
//
//            //Delete Name Individual
//            List<CRSImportDeleteCustodyNameIndividualModel> nameIndividualDelete = new ArrayList<>();
//            nameIndividualDelete = importDeleteCustodyNameIndividualRepository.getAllApproval();
//            if (nameIndividualDelete.isEmpty()) {
//                pModel.addAttribute("alreadyDatanameIndividualDelete", false);
//            } else {
//                pModel.addAttribute("alreadyDatanameIndividualDelete", true);
//                pModel.addAttribute("totalRoleDatanameIndividualDelete", nameIndividualDelete.size());
//                pModel.addAttribute("userRoleDatanameIndividualDelete", nameIndividualDelete);
//            }
//
//            //Name Organization
//            List<CRSImportDeleteCustodyNameOrganizationModel> nameOrganizationDelete = new ArrayList<>();
//            nameOrganizationDelete = importDeleteCustodyNameOrganizationRepository.getAllApproval();
//            if (nameOrganizationDelete.isEmpty()) {
//                pModel.addAttribute("alreadyDatanameOrganizationDelete", false);
//            } else {
//                pModel.addAttribute("alreadyDatanameOrganizationDelete", true);
//                pModel.addAttribute("totalRoleDatanameOrganizationDelete", nameOrganizationDelete.size());
//                pModel.addAttribute("userRoleDatanameOrganizationDelete", nameOrganizationDelete);
//            }
//
//            //Rep Address
//            List<CRSImportDeleteCustodyRepAddressModel> repAddressDelete = new ArrayList<>();
//            repAddressDelete = importDeleteCustodyRepAddressRepository.getAllApproval();
//            if (repAddressDelete.isEmpty()) {
//                pModel.addAttribute("alreadyDatarepAddressDelete", false);
//            } else {
//                pModel.addAttribute("alreadyDatarepAddressDelete", true);
//                pModel.addAttribute("totalRoleDatarepAddressDelete", repAddress.size());
//                pModel.addAttribute("userRoleDatarepAddressDelete", repAddress);
//            }
//
//            //Rep Payment
//            List<CRSImportDeleteCustodyRepPaymentModel> repPaymentDelete = new ArrayList<>();
//            repPaymentDelete = importDeleteCustodyRepPaymentRepository.getAllApproval();
//            if (repPaymentDelete.isEmpty()) {
//                pModel.addAttribute("alreadyDatarepPaymentDelete", false);
//            } else {
//                pModel.addAttribute("alreadyDatarepPaymentDelete", true);
//                pModel.addAttribute("totalRoleDatarepPaymentDelete", repPaymentDelete.size());
//                pModel.addAttribute("userRoleDatarepPaymentDelete", repPaymentDelete);
//            }
//
//
//            pModel.addAttribute("contentPath", "uploadapprovaldeletecrscustody");
//            pModel.addAttribute("contentName", "uploadapprovaldeletecrscustody");
//            UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
//            pModel.addAttribute("var",userRole.getRoleId());
//            List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
//            pModel.addAttribute("roleUserAccess", userAccess);
//            pModel.addAttribute("UserLogin",userRole.getName());
//            pModel.addAttribute("User",userLoginApp);
//
//            auditTrailCustodyService.postAuditTrail(userLoginApp,Operation,AccessScreen,"","");
//            return "index";
//
//        } else {
//            log.info("Cookies Else");
//            return "redirect:/login";
//        }
//    }

    @PostMapping("approval/approvalallpersonal")
    public String approvalallpersonalp(HttpServletRequest pRequest, Model pModel,
                                                  ApproveReject approvalreject,
                                                  RedirectAttributes redirectAttr) throws JAXBException, ParseException, IOException {
        log.info("Request Client : " + pRequest.getRemoteAddr());
        log.info("Request URL : " + pRequest.getRequestURL());
        log.info("Request Method : " + pRequest.getMethod());


        Cookie[] cookies        = pRequest.getCookies();
        String userLoginApp     = Arrays.stream(cookies)
                .filter(a->a.getName().equals("username"))
                .map(c -> c.getValue()).collect(Collectors.joining(", "));
        String Operation        = "Approve";
        String AccessScreen     = "Approve Report Payment Koreksi Pelaporan CRS Custody";
        String UserIP           = HttpUtils.getRequestIP(pRequest);
        int no = 4;
        System.out.println(approvalreject.getApproveReject());
        for(int i=0; i<=approvalreject.getIdUnik().length - 1;i++){
            System.out.println(approvalreject.getIdUnik()[i]);
        }


        redirectAttr.addFlashAttribute("Success","Success!, The corrected file with DocRefIdAccountReport  has been approved.");

        UserRoleModel userRole = UserRoleRepository.findByUserId(userLoginApp);
        pModel.addAttribute("var",userRole.getRoleId());
        List<UserAccess> userAccess = userAccessRepository.findUserAccess(userRole.getRoleId());
        pModel.addAttribute("roleUserAccess", userAccess);
        pModel.addAttribute("UserLogin",userRole.getName());
        pModel.addAttribute("User",userLoginApp);
        return "redirect:/custody/approval";
    }
}
