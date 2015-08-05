package hy.tmc.core;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import fi.helsinki.cs.tmc.langs.domain.RunResult;
import hy.tmc.core.configuration.TmcSettings;
import hy.tmc.core.domain.Course;
import hy.tmc.core.exceptions.TmcCoreException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;


public class Main {
    public static void main(String[] args) throws IOException, TmcCoreException, InterruptedException, ExecutionException {
        File cacheFile = Paths.get("cache").toFile();
        if (!cacheFile.exists()) {
            cacheFile.createNewFile();
        }
        TmcCore core = new TmcCore(cacheFile);
        TmcSettings s = settings();
        
        ListenableFuture<RunResult> f = 
                core.test("/home/ilari/rage/random/2013_ohpeJaOhja/viikko1/Viikko1_002.HeiMaailma", s);
        
        RunResult r = f.get();
        
        System.out.println(r);
        
//        Course testi = new Course("pihlantesti");
//        testi.setDetailsUrl("https://tmc.mooc.fi/staging/org/default/courses/35.json");
//        testi.setId(35);
//        
//        ListenableFuture<List<Exercise>> ff = 
//                core.downloadExercises("/home/ilari/rage/random", "35", s, null);
//        
//        List<Exercise> ees = ff.get();
//        
//        System.out.println("dl ready \\o/");
//        System.out.println(ees);
//
//        System.out.println("update whenever you're ready");
//        new Scanner(System.in).nextLine();
//
//        ListenableFuture<List<Exercise>> f = 
//                core.getNewAndUpdatedExercises(testi, s);
//        List<Exercise> es = f.get();
//        
//        System.out.println("got sth");
//        System.out.println(es);
       
    }
    
    private static TmcSettings settings() {
        return new TmcSettings() {

            @Override
            public String getServerAddress() {
                return "https://tmc.mooc.fi/staging/org/default";
            }

            @Override
            public String getPassword() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public String getUsername() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean userDataExists() {
                return true;
            }

            @Override
            public Optional<Course> getCurrentCourse() {
                Course course = new Course("tmc-testcourse");
                course.setDetailsUrl("https://tmc.mooc.fi/staging/org/default/courses/27.json");
                course.setId(27);
                return Optional.of(course);
            }

            @Override
            public String apiVersion() {return "7";}

            @Override
            public String getFormattedUserData() {
                return "test:1234";}

            @Override
            public String getTmcMainDirectory() {
               return "/home/ilari/ohturojektijuttui/core";}

            @Override
            public String clientName() {
                return "tmc_cli";
            }

            @Override
            public String clientVersion() {
                return "1";
            }
        };
    }
}
