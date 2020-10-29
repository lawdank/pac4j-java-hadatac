package org.hadatac.console.models;


public class DataAcquisitionForm {

    public String newUri;
    public String newPermission;
    public String newOwner;
    public String newParameter;
    public String newSchema;
    public String newStartDate;
    public String newEndDate;

    ////////////////////////
    public String studyId;
    public String daName;
    public String sddName;
    public String deploymentUri;
    public String cellScope;
    public String ownerEmail;
    public String permissionUri;

    public String getNewDataAcquisitionUri() {
        return newUri;
    }
    public void setNewDataAcquisitionUri(String newUri) {
        this.newUri = newUri;
    }

    public String getNewPermission() {
        return newPermission;
    }
    public void setNewPermission(String newPermission) {
        this.newPermission = newPermission;
    }

    public String getNewOwner() {
        return newOwner;
    }
    public void setNewOwner(String newOwner) {
        this.newOwner = newOwner;
    }

    public String getNewParameter() {
        return newParameter;
    }
    public void setNewParameter(String newParameter) {
        this.newParameter = newParameter;
    }

    public String getNewSchema() {
        return newSchema;
    }
    public void setNewSchema(String newSchema) {
        this.newSchema = newSchema;
    }

    public String getNewStartDate() {
        return newStartDate;
    }
    public void setNewStartDate(String newStartDate) {
        this.newStartDate = newStartDate;
    }

    public String getNewEndDate() {
        return newEndDate;
    }
    public void setNewEndDate(String newEndDate) {
        this.newEndDate = newEndDate;
    }

    ////////////////////////

    public String getStudyId() {
        return studyId;
    }

    public String getDaName() {
        return daName;
    }

    public String getSddName() {
        return sddName;
    }

    public String getDeploymentUri() {
        return deploymentUri;
    }

    public String getCellScope() {
        return cellScope;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public String getPermissionUri() {
        return permissionUri;
    }
}
