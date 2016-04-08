package com.presidiohealth.gradle.tasks;

import org.gradle.api.Project;
import org.gradlefx.cli.compiler.CompilerOption;
import org.gradlefx.cli.instructions.flexsdk.ApplicationInstructions;

public class ModulesInstructions extends ApplicationInstructions {

    String applicationPath;
    String modulePath;

    public ModulesInstructions(Project project, String applicationPath, String modulePath) {
        super(project);
        this.applicationPath = applicationPath;
        this.modulePath = modulePath;
    }

    @Override
    public void configure() {

        setDefaultConfigName();

        addSourceDirectories();
        addLocaleSources();
        addLocales();

        addInternalLibraries();
        addExternalLibrariesForApp();
        addMergedLibraries();
        addTheme();

        addAdditionalCompilerOptions();
        addLoadExterns();
        addModuleOutput();

        getCompilerOptions().add(modulePath);
    }
    public void validateFilesExist(Object files, Object configuration) {}

    void addLoadExterns() {
        String reportPath = getProject().getBuildDir().getPath() + "/link-report.xml";
        getCompilerOptions().add(CompilerOption.LOAD_EXTERNS, reportPath);
    }
    void addModuleOutput() {
        addOutput(modulePath.substring(0, modulePath.lastIndexOf(".")) + ".swf");
    }
}

