package com.github.markash.whisk.model;

import java.util.Map;

public class Transformation {

    private String templateName;
    private String resultName;
    private Map<String, Object> substitutions;

    /**
     * @return the resultName
     */
    public String getResultName() {
        return resultName;
    }

    /**
     * @return the templateName
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * @return the substitutions
     */
    public Map<String, Object> getSubstitutions() {
        return substitutions;
    }
    
    /**
     * @param resultName the resultName to set
     */
    public void setResultName(String resultName) {
        this.resultName = resultName;
    }

    /**
     * @param templateName the templateName to set
     */
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    /**
     * @param substitutions the substitutions to set
     */
    public void setSubstitutions(Map<String, Object> substitutions) {
        this.substitutions = substitutions;
    }
}