package com.presidiohealth.gradle.tasks;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.gradlefx.cli.compiler.CompilerResult;
import org.gradlefx.cli.compiler.CompilerResultHandler;
import org.gradlefx.conventions.GradleFxConvention;
import org.gradlefx.tasks.TaskGroups;
import org.gradlefx.tasks.compile.CompileTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class CompileModulesTask extends DefaultTask implements CompileTask {

    public static final String COMPILE_MODULES_NAME = "compileModules";

    protected GradleFxConvention flexConvention;
    protected File modulesDirectory;
    protected File modulesBuildDirectory;
    protected File[] moduleFiles;

    public CompileModulesTask() {
        setDescription("Compiles and packages a projects modules directory if it exists.");
        initialize();
    }
    protected void initialize() {
        setGroup(TaskGroups.BUILD.toString());
        getProject().afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project project) {
                flexConvention = (GradleFxConvention) getProject().getConvention().getPlugins().get("flex");
            }
        });
    }

    @TaskAction
    @Override
    public void compile() {
        modulesDirectory = getProject().file(flexConvention.getSrcDirs().get(0) + "/modules");

        if (!modulesDirectory.exists())
            return;

        moduleFiles = filterModuleFiles(modulesDirectory.listFiles());

        if (moduleFiles.length == 0)
            return;

        modulesBuildDirectory = createModuleBuildDirectory();
        System.out.println(String.format("modules found at application: %s %s.", flexConvention.getMainClass(), modulesDirectory));

        final DefaultTask task = this;
        new MxmlcApplicationWithLinkReport(task, new CompilerResultHandler() {
            @Override
            public void handleResult(CompilerResult result) {
                new MxmlcModules(task).compile();
            }
        }).compile();
    }

    File createModuleBuildDirectory() {
        String path = getProject().getBuildDir().getPath() + "/modules";
        File file;
        try {
            file = new File(path);
            if (file.exists() && file.isDirectory())
                return file;

            if (!file.mkdir())
                throw new RuntimeException("Could not create modules build directory at " + path);
        } catch (Exception e) {
            throw new RuntimeException("Could not create modules build directory at " + path + " " + e.getMessage());
        }
        return file;
    }

    public File getModulesBuildDirectory() {
        return modulesBuildDirectory;
    }

    public File[] getModuleFiles() {
        return moduleFiles;
    }

    public File[] filterModuleFiles(File[] moduleFiles) {
        List<File> result = new ArrayList<File>();
        for (File file : moduleFiles)
            if (file.getName().contains(".mxml"))
                result.add(file);
        return result.toArray(new File[result.size()]);
    }
}
