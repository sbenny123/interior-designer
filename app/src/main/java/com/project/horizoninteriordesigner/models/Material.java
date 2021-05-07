package com.project.horizoninteriordesigner.models;

public class Material {

    private String materialId;
    private String materialName;
    private String materialUrl;


    public Material () {
        // Needed for Firebase
    }

    public Material(String materialId, String materialName, String materialUrl) {
        this.materialId = materialId;
        this.materialName = materialName;
        this.materialUrl = materialUrl;
    }

    public String getMaterialId() {
        return materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public String getMaterialUrl() {
        return materialUrl;
    }
}
