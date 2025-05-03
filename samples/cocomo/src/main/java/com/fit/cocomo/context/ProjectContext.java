package com.fit.cocomo.context;

import com.fit.bru.rule.context.RuleExecutionContext;


public class ProjectContext implements RuleExecutionContext {

    private final SoftwareType softwareType;

    private final double numberLineCode;
    private double projectTypeIndice;
    private double complexityIndice;
    private double environmentIndice;
    private double teamExperienceIndice;
    private double effort;
    private double developmentTime;
    private long requiredStaff;

    public ProjectContext(SoftwareType softwareType, double numberLineCode) {
        this.softwareType = softwareType;
        this.numberLineCode = numberLineCode;
    }

    public SoftwareType getSoftwareType() {
        return softwareType;
    }


    public double getNumberLineCode() {
        return numberLineCode;
    }

    public double getProjectTypeIndice() {
        return projectTypeIndice;
    }

    public void setProjectTypeIndice(double projectTypeIndice) {
        this.projectTypeIndice = projectTypeIndice;
    }

    public double getComplexityIndice() {
        return complexityIndice;
    }

    public void setComplexityIndice(double complexityIndice) {
        this.complexityIndice = complexityIndice;
    }

    public double getEnvironmentIndice() {
        return environmentIndice;
    }

    public void setEnvironmentIndice(double environmentIndice) {
        this.environmentIndice = environmentIndice;
    }

    public double getTeamExperienceIndice() {
        return teamExperienceIndice;
    }

    public void setTeamExperienceIndice(double teamExperienceIndice) {
        this.teamExperienceIndice = teamExperienceIndice;
    }

    public double getEffort() {
        return effort;
    }

    public void setEffort(double effort) {
        this.effort = effort;
    }

    public double getDevelopmentTime() {
        return developmentTime;
    }

    public void setDevelopmentTime(double developmentTime) {
        this.developmentTime = developmentTime;
    }

    public long getRequiredStaff() {
        return requiredStaff;
    }

    public void setRequiredStaff(long requiredStaff) {
        this.requiredStaff = requiredStaff;
    }
}
