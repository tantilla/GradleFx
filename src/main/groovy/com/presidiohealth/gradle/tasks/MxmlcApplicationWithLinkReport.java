package com.presidiohealth.gradle.tasks;


import org.gradle.api.Task;
import org.gradlefx.cli.compiler.*;
import org.gradlefx.cli.instructions.CompilerInstructionsBuilder;
import org.gradlefx.tasks.compile.CompileTaskDelegate;

import java.io.File;

public class MxmlcApplicationWithLinkReport extends CompileTaskDelegate {

    CompileModulesTask task;
    CompilerResultHandler resultHandler;

    public MxmlcApplicationWithLinkReport(Task task, CompilerResultHandler resultHandler) {
        super(task);
        this.task = (CompileModulesTask) getTask();
        this.resultHandler = resultHandler;
    }

    @Override
    public void compile() {
        AntBasedCompilerProcess createCompilerProcess = createCompilerProcess(prepareCompilerSettings());
        createCompilerProcess.setCompilerResultHandler(resultHandler);
        createCompilerProcess.compile();
    }

    CompilerSettings prepareCompilerSettings() {
        CompilerOptions compilerInstructions = createCompilerInstructionsBuilder().buildInstructions();
        return new CompilerSettings(compilerInstructions, CompilerJar.mxmlc);
    }

    AntBasedCompilerProcess createCompilerProcess(CompilerSettings requirements) {
        AntBasedCompilerProcess compilerProcess = new AntBasedCompilerProcess(
                getTask().getAnt(),
                requirements.getCompilerJar(), new File(getFlexConvention().getFlexHome()));

        compilerProcess.setJvmArguments(getFlexConvention().getJvmArguments());
        compilerProcess.setCompilerOptions(requirements.getCompilerOptions());
        return compilerProcess;
    }

    CompilerInstructionsBuilder createCompilerInstructionsBuilder() {
        return new LinkReportInstructions(getTask().getProject());
    }
}
