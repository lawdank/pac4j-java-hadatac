package data.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import entity.pojo.ConsoleStore;
import entity.pojo.STR;
import entity.pojo.DataAcquisitionSchema;
import entity.pojo.Dataset;
import entity.pojo.Deployment;
import entity.pojo.Detector;
import entity.pojo.Instrument;
import entity.pojo.Platform;
import entity.pojo.Study;
import util.ConfigProp;

import com.typesafe.config.ConfigFactory;

public class DataFactory {

    public static String DEPLOYMENT_ABBREV = "DP";

    public static String DATA_COLLECTION_ABBREV = "DC";

    public static String DATASET_ABBREV = "DS";

    public static String CONSOLE_ID = "00000001";

    public static final String kbPrefix = ConfigProp.getKbPrefix();

    public static STR createDataAcquisition(int triggeringEvent,
                                            String dataCollectionUri,
                                            String deploymentUri,
                                            String parameter,
                                            String ownerUri) {
        Deployment deployment = Deployment.find(deploymentUri);
        if (null == deployment) {
            return null;
        }

        STR dataAcquisition = new STR();
        dataAcquisition.setUri(dataCollectionUri);
        dataAcquisition.setOwnerUri(ownerUri);
        dataAcquisition.setParameter(parameter);
        dataAcquisition.setPermissionUri(ownerUri);
        dataAcquisition.setTriggeringEvent(triggeringEvent);
        dataAcquisition.setPlatformUri(deployment.getPlatform().getUri());
        dataAcquisition.setInstrumentUri(deployment.getInstrument().getUri());
        dataAcquisition.setPlatformName(deployment.getPlatform().getLabel());
        dataAcquisition.setInstrumentModel(deployment.getInstrument().getLabel());
        dataAcquisition.setStartedAtXsdWithMillis(deployment.getStartedAt());
        dataAcquisition.setDeploymentUri(deploymentUri);

        return dataAcquisition;
    }

    public static Dataset createDataset() {
        Dataset dataset = null;
        return dataset;
    }

    public static Deployment createDeployment(String deploymentUri, String platformUri, String instrumentUri, List<String> detectorUri, String startedAt, String type) {
        Deployment deployment;
        if (type.equalsIgnoreCase("LEGACY")) {
            deployment = Deployment.createLegacy(deploymentUri);
        }
        else {
            deployment = Deployment.create(deploymentUri);
        }

        deployment.setPlatform(Platform.find(platformUri));
        deployment.setInstrument(Instrument.find(instrumentUri));
        for (int i = 0; i < detectorUri.size(); i++) {
            deployment.getDetectors().add(Detector.find(detectorUri.get(i)));
        }
        deployment.setStartedAtXsd(startedAt);

        return deployment;
    }

    public static Study createStudy(String studyUri, List<String> dataAcquisionUris, String startedAt) {
        Study study;

        study = new Study();

        study.setUri(studyUri);
        study.setStartedAtXsd(startedAt);

        return study;
    }

    public static Deployment createLegacyDeployment(String deploymentUri, String platformUri, String instrumentUri, List<String> detectorUri, String startedAt) {
        Deployment deployment = Deployment.createLegacy(deploymentUri);

        deployment.setPlatform(Platform.find(platformUri));
        deployment.setInstrument(Instrument.find(instrumentUri));
        for (int i = 0; i < detectorUri.size(); i++) {
            deployment.getDetectors().add(Detector.find(detectorUri.get(i)));
        }
        deployment.setStartedAtXsd(startedAt);
        deployment.saveToTripleStore();

        return deployment;
    }

    public static DataAcquisitionSchema createDataAcquisitionSchema(String dasName) {
        String dasUri = kbPrefix + "SDD-" + dasName;
        String dasLabel = "SDD-" + dasName;
        DataAcquisitionSchema das = DataAcquisitionSchema.create(dasUri);
        das.setLabel(dasLabel);
        das.saveToTripleStore();
        return das;
    }

    public static STR getActiveDataAcquisition(String deploymentUri) {
        List<STR> list;
        Deployment deployment = Deployment.find(deploymentUri);
        list = STR.find(deployment, false);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public static long getNextDynamicMetadataId() {
        try {
            ConsoleStore consoleStore = ConsoleStore.find();
            if (null == consoleStore) {
                consoleStore = new ConsoleStore();
                consoleStore.setId(0);
                consoleStore.setTimestamp(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
                consoleStore.setLastDynamicMetadataId(1);
            } else {
                consoleStore.setLastDynamicMetadataId(consoleStore.getLastDynamicMetadataId() + 1);
            }
            consoleStore.saveToSolr();
            return consoleStore.getLastDynamicMetadataId();
        } catch (Exception e) {
            System.out.println("[ERROR] DataFactory.getNextDynamicMetadataId() - e.Message: " + e.getMessage());
        }

        return -1;
    }

    public static String getNextURI(String category) {
        String metadataId = Long.toHexString(DataFactory.getNextDynamicMetadataId());
        String host = ConfigFactory.load().getString("hadatac.console.host");
        for (int i = metadataId.length(); i <= 8; i++) {
            metadataId = "0" + metadataId;
        }
        return host + "/hadatac/kb/" + category + "/" + CONSOLE_ID + "/" + metadataId ;
    }

    public static String getNextDatasetURI(String base) {
        String metadataId = Long.toHexString(DataFactory.getNextDynamicMetadataId());
        for (int i = metadataId.length(); i <= 8; i++) {
            metadataId = "0" + metadataId;
        }

        return base + "/" + metadataId ;
    }
}
