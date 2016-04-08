package com.presidiohealth.gradle.tasks;

import org.gradle.api.Project;
import org.gradlefx.cli.compiler.CompilerOption;
import org.gradlefx.cli.instructions.flexsdk.ApplicationInstructions;
import org.gradlefx.conventions.FrameworkLinkage;

public class LinkReportInstructions extends ApplicationInstructions {

    public LinkReportInstructions(Project project) {
        super(project);
    }

    @Override
    public void configure() {
        setDefaultConfigName();

        FrameworkLinkage linkage = getFlexConvention().getFrameworkLinkage();
        if (!linkage.isCompilerDefault(getFlexConvention().getType())) {
            reapplyFrameworkRslsAccordingToFrameworkLinkage();
        }

        addSourceDirectories();
        addLocaleSources();
        addLocales();

        addInternalLibraries();
        addExternalLibrariesForApp();
        addMergedLibraries();
        addTheme();
        addRSLs();
        addFrameworkRsls();

        addAdditionalCompilerOptions();
        addOutput();
        addLinkReport();

        addMainClass();
    }

    public void validateFilesExist(Object files, Object configuration) {}

    public void addLinkReport() {
        String reportPath = getProject().getBuildDir().getPath() + "/link-report.xml";
        System.out.println("creating link report: " + reportPath);
        getCompilerOptions().add(CompilerOption.LINK_REPORT, reportPath);
    }
}
