package com.presidiohealth.gradle.tasks;


import org.gradle.api.Task;
import org.gradlefx.cli.compiler.*;
import org.gradlefx.cli.instructions.CompilerInstructionsBuilder;
import org.gradlefx.tasks.compile.CompileTaskDelegate;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class MxmlcModules extends CompileTaskDelegate {

    CompileModulesTask task;
    public MxmlcModules(Task task) {
        super(task);
        this.task = (CompileModulesTask) getTask();
    }

    @Override
    public void compile() {
        String applicationPath = task.getProject().getPath() + "/" + getFlexConvention().getMainClass() + ".mxml";
        for (final File module : task.getModuleFiles()) {
            System.out.println("compiling module: " + module.getPath());
            AntBasedCompilerProcess compilerProcess = createCompilerProcess(prepareCompilerSettings(applicationPath, module.getPath()));
            compilerProcess.setCompilerResultHandler(new CompilerResultHandler() {
                @Override
                public void handleResult(CompilerResult result) {
                    copyModuleSwfToBuildDirectory(module);
                }
            });
            compilerProcess.compile();
        }
    }

    CompilerSettings prepareCompilerSettings(String applicationPath, String modulePath) {
        CompilerOptions compilerInstructions = createCompilerInstructionsBuilder(applicationPath, modulePath).buildInstructions();
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

    CompilerInstructionsBuilder createCompilerInstructionsBuilder(String applicationPath, String modulePath) {
        return new ModulesInstructions(getTask().getProject(), applicationPath, modulePath);
    }

    void copyModuleSwfToBuildDirectory(File module) {
        try {
            Path moduleSwf = FileSystems.getDefault().getPath(module.getPath().substring(0, module.getPath().indexOf(".")) + ".swf");
            Path modules = FileSystems.getDefault().getPath(task.getModulesBuildDirectory().getPath());
            Files.move(moduleSwf, modules.resolve(moduleSwf.getFileName()), REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
