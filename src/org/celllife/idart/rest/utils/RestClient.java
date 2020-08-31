package org.celllife.idart.rest.utils;

import model.manager.*;
import model.nonPersistent.Autenticacao;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.*;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.rest.ApiAuthRest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;


/**
 * @author helio.machabane
 */
public class RestClient {

    private static Logger log = Logger.getLogger(RestClient.class);

    Properties prop = new Properties();
    //InputStream input = null;

    File input = new File("jdbc.properties");
    File myFile = new File("jdbc_auto_generated.properties");

    Properties prop_dynamic = new Properties();


    //SET VALUE FOR CONNECT TO OPENMRS
    public RestClient() {

        try {
            prop_dynamic.load(new FileInputStream(myFile));
            prop.load(new FileInputStream(input));
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        ApiAuthRest.setURLBase(prop.getProperty("urlBase"));
        ApiAuthRest.setUsername(prop_dynamic.getProperty("userName"));
        //ApiAuthRest.setPassword(prop_dynamic.getProperty("password"));
        ApiAuthRest.setPassword(Autenticacao.senhaTemporaria);
    }

    public boolean postOpenMRSEncounter(String encounterDatetime, String nidUuid, String encounterType, String strFacilityUuid,
                                        String filaUuid, String providerUuid, String regimeUuid,
                                        String strRegimenAnswerUuid, String dispensedAmountUuid, List<PrescribedDrugs> prescribedDrugs,
                                        List<PackagedDrugs> packagedDrugs, String dosageUuid, String returnVisitUuid, String strNextPickUp) throws Exception {

        StringEntity inputAddPerson = null;

        String packSize = null;

        String dosage;

        String dosage_1;

        String customizedDosage = null;

        if (prescribedDrugs.size() == 1) {

            //Dispensed amount
            packSize = String.valueOf(packagedDrugs.get(0).getAmount());

            //Dosage
            dosage = String.valueOf(prescribedDrugs.get(0).getTimesPerDay());

            customizedDosage = iDartProperties.TOMAR + String.valueOf((int) (prescribedDrugs.get(0).getAmtPerTime()))
                    + iDartProperties.COMP + dosage + iDartProperties.VEZES_DIA;

            inputAddPerson = new StringEntity(
                    "{\"encounterDatetime\": \"" + encounterDatetime + "\", \"patient\": \"" + nidUuid + "\", \"encounterType\": \"" + encounterType + "\", "
                            + "\"location\":\"" + strFacilityUuid + "\", \"form\":\"" + filaUuid + "\", \"encounterProviders\":[{\"provider\":\"" + providerUuid + "\", \"encounterRole\":\"a0b03050-c99b-11e0-9572-0800200c9a66\"}], "
                            + "\"obs\":[{\"person\":\"" + nidUuid + "\",\"obsDatetime\":\"" + encounterDatetime + "\",\"concept\":"
                            + "\"" + regimeUuid + "\",\"value\":\"" + strRegimenAnswerUuid + "\", \"comment\":\"IDART\"},{\"person\":"
                            + "\"" + nidUuid + "\",\"obsDatetime\":\"" + encounterDatetime + "\",\"concept\":\"" + dispensedAmountUuid + "\","
                            + "\"value\":\"" + packSize + "\",\"comment\":\"IDART\"},{\"person\":\"" + nidUuid + "\",\"obsDatetime\":\"" + encounterDatetime + "\",\"concept\":"
                            + "\"" + dosageUuid + "\",\"value\":\"" + customizedDosage + "\",\"comment\":\"IDART\"},{\"person\":\"" + nidUuid + "\","
                            + "\"obsDatetime\":\"" + encounterDatetime + "\",\"concept\":\"" + returnVisitUuid + "\",\"value\":\"" + strNextPickUp + "\",\"comment\":\"IDART\"}]}"
                    , "UTF-8");

            System.out.println(IOUtils.toString(inputAddPerson.getContent()));
        } else if (prescribedDrugs.size() > 1) {

            //Dosage
            dosage = String.valueOf(prescribedDrugs.get(0).getTimesPerDay());

            String customizedDosage_0 = iDartProperties.TOMAR + String.valueOf((int) (prescribedDrugs.get(0).getAmtPerTime()))
                    + iDartProperties.COMP + dosage + iDartProperties.VEZES_DIA;

            //Dosage
            dosage_1 = String.valueOf(prescribedDrugs.get(1).getTimesPerDay());

            String customizedDosage_1 = iDartProperties.TOMAR + String.valueOf((int) (prescribedDrugs.get(1).getAmtPerTime()))
                    + iDartProperties.COMP + dosage_1 + iDartProperties.VEZES_DIA;

            inputAddPerson = new StringEntity(
                    "{\"encounterDatetime\": \"" + encounterDatetime + "\", \"patient\": \"" + nidUuid + "\", \"encounterType\": \"" + encounterType + "\", "
                            + "\"location\":\"" + strFacilityUuid + "\", \"form\":\"" + filaUuid + "\", \"encounterProviders\":[{\"provider\":\"" + providerUuid + "\", \"encounterRole\":\"a0b03050-c99b-11e0-9572-0800200c9a66\"}], "
                            + "\"obs\":[{\"person\":\"" + nidUuid + "\",\"obsDatetime\":\"" + encounterDatetime + "\",\"concept\":"
                            + "\"" + regimeUuid + "\",\"value\":\"" + strRegimenAnswerUuid + "\",\"comment\":\"IDART\"},{\"person\":"
                            + "\"" + nidUuid + "\",\"obsDatetime\":\"" + encounterDatetime + "\",\"concept\":\"" + dispensedAmountUuid + "\","
                            + "\"value\":\"" + String.valueOf(packagedDrugs.get(1).getAmount()) + "\",\"comment\":\"IDART\"},{\"person\":\"" + nidUuid + "\",\"obsDatetime\":\"" + encounterDatetime + "\",\"concept\":\"" + dispensedAmountUuid + "\","
                            + "\"value\":\"" + String.valueOf(packagedDrugs.get(0).getAmount()) + "\",\"comment\":\"IDART\"},{\"person\":\"" + nidUuid + "\",\"obsDatetime\":\"" + encounterDatetime + "\",\"concept\":"
                            + "\"" + dosageUuid + "\",\"value\":\"" + customizedDosage_0 + "\",\"comment\":\"IDART\"},{\"person\":\"" + nidUuid + "\",\"obsDatetime\":\"" + encounterDatetime + "\",\"concept\":"
                            + "\"" + dosageUuid + "\",\"value\":\"" + customizedDosage_1 + "\",\"comment\":\"IDART\"},{\"person\":\"" + nidUuid + "\","
                            + "\"obsDatetime\":\"" + encounterDatetime + "\",\"concept\":\"" + returnVisitUuid + "\",\"value\":\"" + strNextPickUp + "\",\"comment\":\"IDART\"}]}"
                    , "UTF-8");
        }

        inputAddPerson.setContentType("application/json");
        //log.info("AddPerson = " + ApiAuthRest.getRequestPost("encounter",inputAddPerson));
        return ApiAuthRest.getRequestPost("encounter", inputAddPerson);
    }

    public boolean postOpenMRSPatient(String gender, String firstName, String middleName, String lastName, String birthDate, String nid) throws Exception {

        StringEntity inputAddPatient;

        String openmrsJSON = "";

        if (birthDate.isEmpty()) {

            openmrsJSON = "{\"person\":"
                    + "{"
                    + "\"gender\": \"" + gender + "\","
                    + "\"names\":"
                    + "[{\"givenName\": \"" + firstName + "\", \"middleName\": \"" + middleName + "\", \"familyName\": \"" + lastName + "\"}]"
                    + "},"
                    + "\"identifiers\":"
                    + "["
                    + "{"
                    + "\"identifier\": \"" + nid + "\", \"identifierType\": \"e2b966d0-1d5f-11e0-b929-000c29ad1d07\","
                    + "\"location\": \"" + prop.getProperty("location") + "\", \"preferred\": \"true\""
                    + "}"
                    + "]"
                    + "}";
        } else {
            openmrsJSON = "{\"person\":"
                    + "{"
                    + "\"gender\": \"" + gender + "\","
                    + "\"names\":"
                    + "[{\"givenName\": \"" + firstName + "\", \"middleName\": \"" + middleName + "\", \"familyName\": \"" + lastName + "\"}], \"birthdate\": \"" + birthDate + "\""
                    + "},"
                    + "\"identifiers\":"
                    + "["
                    + "{"
                    + "\"identifier\": \"" + nid + "\", \"identifierType\": \"e2b966d0-1d5f-11e0-b929-000c29ad1d07\","
                    + "\"location\": \"" + prop.getProperty("location") + "\", \"preferred\": \"true\""
                    + "}"
                    + "]"
                    + "}";
        }

        inputAddPatient = new StringEntity(openmrsJSON, "UTF-8");

        inputAddPatient.setContentType("application/json");
        //log.info("AddPerson = " + ApiAuthRest.getRequestPost("encounter",inputAddPerson));
        return ApiAuthRest.getRequestPost("patient", inputAddPatient);
    }

    public String getOpenMRSResource(String resourceParameter) {
        String resource = null;
        try {
            resource = ApiAuthRest.getRequestGet(resourceParameter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resource;
    }

    public String getOpenMRSReportingRest(String resourceParameter) {
        ApiAuthRest.setURLReportingBase(prop.getProperty("urlBaseReportingRest"));
        String resource = null;
        try {
            resource = ApiAuthRest.getReportingRequestGet(resourceParameter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resource;
    }

    public static void setOpenmrsPatients() {

        RestClient restClient = new RestClient();

        boolean postOpenMrsEncounterStatus;

        Session session = HibernateUtil.getNewSession();
        Transaction tx = session.beginTransaction();

        try {

            List<SyncOpenmrsPatient> syncOpenmrsPatients = PatientManager.getAllSyncOpenmrsPatientReadyToSave(session);
            if (!syncOpenmrsPatients.isEmpty()) {

                for (SyncOpenmrsPatient patientToSave : syncOpenmrsPatients) {
                    try {
                        String uuid = null;
                        Patient pacient = PatientManager.getPatient(session, patientToSave.getPatientid());

                        if (pacient != null) {
                            uuid = getUUidFromOpenmrs(pacient.getPatientId());

                            if (uuid == null) {

                                postOpenMrsEncounterStatus = restClient.postOpenMRSPatient(pacient.getSex() + "", pacient.getFirstNames(), ".", pacient.getLastname(),
                                        RestUtils.castDateToString(pacient.getDateOfBirth()), pacient.getPatientId());

                                if (postOpenMrsEncounterStatus) {
                                    uuid = getUUidFromOpenmrs(pacient.getPatientId());
                                }
                            }
                            pacient.setUuidopenmrs(uuid);
                            PatientManager.savePatient(session, pacient);
                        } else
                            log.trace(new Date() + ": O Paciente [" + patientToSave.getFirstnames() + " " + patientToSave.getLastname() + " com NID: " + patientToSave.getPatientid() + "] foi removido");

                        patientToSave.setSyncstatus('E');
                        PatientManager.saveSyncOpenmrsPatien(session, patientToSave);

                        break;
                    } catch (Exception e) {
                        log.trace(new Date() + ": Erro ao gravar informacao do Paciente [" + patientToSave.getFirstnames() + " " + patientToSave.getLastname() + " com NID: " + patientToSave.getPatientid() + "] verifique o acesso do user ao openmrs ou contacte o administrador");
                    } finally {
                        continue;
                    }
                }
            } else {
                log.trace(new Date() + ": INFO - Nenhumm paciente por enviar foi encontrado");
            }
            tx.commit();
            session.flush();
            session.close();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
                session.close();
            }
            log.trace("Error :" + e);
        }
    }

    public static void setOpenmrsPatientFila() {

        Session session = HibernateUtil.getNewSession();
        Transaction tx = session.beginTransaction();

        try {
            List<SyncOpenmrsDispense> syncOpenmrsDispenses = PrescriptionManager.getAllSyncOpenmrsDispenseReadyToSave(session);
            if (!syncOpenmrsDispenses.isEmpty()) {

                for (SyncOpenmrsDispense dispense : syncOpenmrsDispenses) {
                    try {
                        Prescription prescription = PackageManager.getPrescription(session, dispense.getPrescription().getPrescriptionId());

                        if (prescription != null) {
                            restFilaToOpenMRS(session, dispense);
                        } else {
                            log.trace(new Date() + ": INFO - A Receita com o codigo: [" + dispense.getPrescription().getPrescriptionId() + "] foi removido");
                            dispense.setSyncstatus('E');
                        }
                        PrescriptionManager.saveSyncOpenmrsPatienFila(session, dispense);
                        break;
                    } catch (Exception e) {
                        log.trace(new Date() + ": INFO - Erro ao gravar levantamento do Paciente com NID: [" + dispense.getPrescription().getPatient().getPatientId() + "], verifique o acesso do user ao openmrs ou contacte o administrador");
                    } finally {
                        continue;
                    }
                }
            } else {
                log.trace(new Date() + ": INFO - Nenhumm levantamento enviado para Openmrs foi encontrado");
            }
            tx.commit();
            session.flush();
            session.close();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
                session.close();
            }
            log.trace("Error :" + e);
        }
    }

    public static String getUUidFromOpenmrs(String patientId) {

        String resource = new RestClient().getOpenMRSResource(iDartProperties.REST_GET_PATIENT + StringUtils.replace(patientId.toUpperCase(), " ", "%20"));

        JSONObject _jsonObject = new JSONObject(resource);

        String personUuid = null;

        JSONArray _jsonArray = (JSONArray) _jsonObject.get("results");

        for (int i = 0; i < _jsonArray.length(); i++) {
            JSONObject results = (JSONObject) _jsonArray.get(i);
            personUuid = (String) results.get("uuid");
        }

        return personUuid;
    }


    public static void restFilaToOpenMRS(Session session, SyncOpenmrsDispense dispense) {

        // Add interoperability with OpenMRS through Rest Web Services
        RestClient restClient = new RestClient();

        boolean postOpenMrsEncounterStatus = false;

        String nidRest = restClient.getOpenMRSResource(iDartProperties.REST_GET_PATIENT + StringUtils.replace(dispense.getNid(), " ", "%20"));

        JSONObject jsonObject = new JSONObject(nidRest);
        JSONArray _jsonArray = (JSONArray) jsonObject.get("results");
        String nidUuid = null;

        for (int i = 0; i < _jsonArray.length(); i++) {
            JSONObject results = (JSONObject) _jsonArray.get(i);
            nidUuid = (String) results.get("uuid");
        }

        if(dispense.getUuid() == null)
            dispense.setUuid(nidUuid);

        String uuid = dispense.getUuid();
        if (uuid != null && !uuid.isEmpty()) {
            uuid = dispense.getUuid();
        } else {
            log.trace(new Date() + ": INFO - O NID [" + dispense.getNid() + "] foi alterado no OpenMRS ou não possui UUID."
                    + " Por favor actualize o NID na Administração do Paciente usando a opção Atualizar um Paciente Existente.");
            return;
        }

        String openrsMrsReportingRest = restClient.getOpenMRSReportingRest(iDartProperties.REST_GET_REPORTING_REST + uuid);

        JSONObject jsonReportingRest = new JSONObject(openrsMrsReportingRest);
        JSONArray jsonReportingRestArray = (JSONArray) jsonReportingRest.get("members");


        if (jsonReportingRestArray.length() < 1) {

            log.trace(new Date() + ": INFO - O NID [" + dispense.getNid() + "]  não se encontra no estado ACTIVO NO PROGRAMA/TRANSFERIDO DE. " +
                    "Actualize primeiro o estado do paciente no OpenMRS..");

            return;
        }

        String response = restClient.getOpenMRSResource(iDartProperties.REST_GET_PROVIDER + StringUtils.replace(dispense.getProvider(), " ", "%20"));

        String providerUuid = response.substring(21, 57);

        // Location
        String strFacility = restClient.getOpenMRSResource(iDartProperties.REST_GET_LOCATION + StringUtils.replace(dispense.getStrFacility(), " ", "%20"));

        // Health Facility
        String strFacilityUuid = strFacility.substring(21, 57);

        Packages newPack = PackageManager.getLastPackageOnScript(dispense.getPrescription());

        try {

            postOpenMrsEncounterStatus = restClient.postOpenMRSEncounter(dispense.getStrPickUp(), uuid, iDartProperties.ENCOUNTER_TYPE_PHARMACY,
                    strFacilityUuid, iDartProperties.FORM_FILA, providerUuid, iDartProperties.REGIME, dispense.getRegimenAnswer(),
                    iDartProperties.DISPENSED_AMOUNT, dispense.getPrescription().getPrescribedDrugs(), newPack.getPackagedDrugs(), iDartProperties.DOSAGE,
                    iDartProperties.VISIT_UUID, dispense.getStrNextPickUp());

            log.trace("Criou o fila no openmrs para o paciente " + dispense.getNid() + ": " + postOpenMrsEncounterStatus);

            if (postOpenMrsEncounterStatus) {
                dispense.setSyncstatus('E');
                dispense.setUuid(uuid);
                OpenmrsErrorLog errorLog = OpenmrsErrorLogManager.getErrorLog(session, newPack.getPrescription());
                if (errorLog != null)
                    OpenmrsErrorLogManager.removeErrorLog(session, errorLog);
            }

        } catch (Exception e) {
            log.trace("Nao foi criado o fila no openmrs para o paciente " + dispense.getNid() + ": " + postOpenMrsEncounterStatus);
            OpenmrsErrorLog errorLog = OpenmrsErrorLogManager.getErrorLog(session, newPack.getPrescription());
            if (errorLog == null) {
                errorLog = new OpenmrsErrorLog();
                errorLog.setPatient(newPack.getPrescription().getPatient());
                errorLog.setPrescription(newPack.getPrescription());
                errorLog.setPickupdate(newPack.getPickupDate());
                errorLog.setReturnpickupdate(RestUtils.castStringToDate(dispense.getStrNextPickUp()));
                errorLog.setErrordescription(e.getMessage() + "\nHouve um problema ao salvar o pacote de medicamentos para o paciente " + dispense.getNid() + ". " + "Por favor contacte o SIS.");
                errorLog.setDatacreated(new Date());
                OpenmrsErrorLogManager.saveOpenmrsRestLog(session, errorLog);
            }

        }
    }

}
