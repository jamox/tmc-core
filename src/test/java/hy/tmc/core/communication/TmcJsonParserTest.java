package hy.tmc.core.communication;

import com.google.common.base.Optional;

import hy.tmc.core.CoreTestSettings;
import hy.tmc.core.domain.Course;
import hy.tmc.core.domain.submission.SubmissionResult;
import hy.tmc.core.exceptions.TmcCoreException;
import hy.tmc.core.testhelpers.ExampleJson;
import java.io.IOException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import static org.powermock.api.mockito.PowerMockito.mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(UrlCommunicator.class)
public class TmcJsonParserTest {

    private CoreTestSettings settings;
    private UrlCommunicator urlCommunicator;
    private TmcJsonParser tmcJsonParser;
    /**
     * Mocks UrlCommunicator.
     */
    @Before
    public void setup() throws IOException, TmcCoreException {
        urlCommunicator = mock(UrlCommunicator.class);
        settings = new CoreTestSettings();
        settings.setUsername("chang");
        settings.setPassword("rajani");
        tmcJsonParser = new TmcJsonParser(urlCommunicator, settings);
        PowerMockito.mockStatic(UrlCommunicator.class);
        
        HttpResult fakeResult = new HttpResult(ExampleJson.allCoursesExample, 200, true);
        
        Mockito.when(urlCommunicator.makeGetRequest(anyString(), anyString()))
                .thenReturn(fakeResult);
        Mockito.when(urlCommunicator.makeGetRequestWithAuthentication(anyString()))
                .thenReturn(fakeResult);
    }
    
    
    private void mockSubmissionUrl() throws IOException, TmcCoreException {
        HttpResult fakeResult = new HttpResult(ExampleJson.successfulSubmission, 200, true);
        Mockito.when(urlCommunicator.makeGetRequest(anyString(), anyString()))
                .thenReturn(fakeResult);
        Mockito.when(urlCommunicator.makeGetRequestWithAuthentication(anyString()))
                .thenReturn(fakeResult);
    }

        
    @Test
    public void getsExercisesCorrectlyFromCourseJson() throws IOException, TmcCoreException {
        HttpResult fakeResult = new HttpResult(ExampleJson.courseExample, 200, true);
        Mockito.when(urlCommunicator.makeGetRequestWithAuthentication(eq("ankka")))
                .thenReturn(fakeResult);
        String names = tmcJsonParser.getExerciseNames("ankka");
        
        assertTrue(names.contains("viikko1-Viikko1_001.Nimi"));
        assertTrue(names.contains("viikko1-Viikko1_002.HeiMaailma"));
        assertTrue(names.contains("viikko1-Viikko1_003.Kuusi"));
    }

    @Test
    public void getsLastExerciseOfCourseJson() throws IOException, TmcCoreException {
        HttpResult fakeResult = new HttpResult(ExampleJson.courseExample, 200, true);
        Mockito.when(urlCommunicator.makeGetRequestWithAuthentication(eq("ankka")))
                .thenReturn(fakeResult);
        String names = tmcJsonParser.getExerciseNames("ankka");
        

        assertTrue(names.contains("viikko11-Viikko11_147.Laskin"));
    }

    @Test
    public void parsesSubmissionUrlFromJson() throws IOException, TmcCoreException {
        HttpResult fakeResult = new HttpResult(ExampleJson.submitResponse, 200, true);
        Mockito.when(urlCommunicator.makeGetRequestWithAuthentication(anyString()))
                .thenReturn(fakeResult);
        assertEquals("http://127.0.0.1:8080/submissions/1781.json?api_version=7", tmcJsonParser.getSubmissionUrl(fakeResult));
    }

    @Test
    public void parsesPasteUrlFromJson() throws IOException, TmcCoreException {
        HttpResult fakeResult = new HttpResult(ExampleJson.pasteResponse, 200, true);
        Mockito.when(urlCommunicator.makeGetRequest(Mockito.anyString(),
                                Mockito.anyString()))
                .thenReturn(fakeResult);
        assertEquals("https://tmc.mooc.fi/staging/paste/ynpw7_mZZGk3a9PPrMWOOQ", tmcJsonParser.getPasteUrl(fakeResult));
    }
    
    String realAddress = "http://real.address.fi";

   

    private void mockCourse(String url) throws IOException, TmcCoreException {
        HttpResult fakeResult = new HttpResult(ExampleJson.courseExample, 200, true);
        Mockito.when(urlCommunicator.makeGetRequestWithAuthentication(eq(url)))
                .thenReturn(fakeResult);
    }

    @Test
    public void getsExercisesCorrectlyFromCourseJSON() throws IOException, TmcCoreException {
        mockCourse(realAddress);
        String names = tmcJsonParser.getExerciseNames(realAddress);

        assertTrue(names.contains("viikko1-Viikko1_001.Nimi"));
        assertTrue(names.contains("viikko1-Viikko1_002.HeiMaailma"));
        assertTrue(names.contains("viikko1-Viikko1_003.Kuusi"));
    }

    @Test
    public void getsLastExerciseOfCourseJSON() throws IOException, TmcCoreException {
        mockCourse(realAddress);
        String names = tmcJsonParser.getExerciseNames(realAddress);

        assertTrue(names.contains("viikko11-Viikko11_147.Laskin"));
    }

    @Test
    public void canFetchOneCourse() throws IOException, TmcCoreException {
        HttpResult fakeResult = new HttpResult(ExampleJson.courseExample, 200, true);
        Mockito.when(urlCommunicator.makeGetRequestWithAuthentication(contains("/courses/3")))
                .thenReturn(fakeResult);

        Optional<Course> course = tmcJsonParser.getCourse(3);
        assertTrue(course.isPresent());
        assertEquals("2013_ohpeJaOhja", course.get().getName());

    }
    
    @Test
    public void canFetchSubmissionData() throws IOException, TmcCoreException {
        mockSubmissionUrl();
        SubmissionResult result = tmcJsonParser.getSubmissionResult("http://real.address.fi");
        assertNotNull(result);
        assertEquals("2014-mooc-no-deadline", result.getCourse());
    }

}
