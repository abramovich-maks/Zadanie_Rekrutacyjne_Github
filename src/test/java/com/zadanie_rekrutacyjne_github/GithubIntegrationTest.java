package com.zadanie_rekrutacyjne_github;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
class GithubIntegrationTest {

    public static final String WIRE_MOCK_HOST = "http://localhost:";

    @DynamicPropertySource
    static void propertyOverride(DynamicPropertyRegistry registry) {
        registry.add("github.service.url", () -> WIRE_MOCK_HOST + wireMockServer.getPort());
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @RegisterExtension
    public static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @Test
    void should_return_repositories_for_existing_user() throws Exception {
        wireMockServer.stubFor(WireMock.get(urlEqualTo("/users/abramovich-maks/repos"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withFixedDelay(1000)
                        .withBody("""
                                [
                                  {
                                    "name": "Lotto",
                                    "owner": { "login": "abramovich-maks" },
                                    "fork": false
                                  },
                                  {
                                    "name": "JobOffers",
                                    "owner": { "login": "abramovich-maks" },
                                    "fork": false
                                  },
                                  {
                                    "name": "ThisIsFork",
                                    "owner": { "login": "abramovich-maks" },
                                    "fork": true
                                  }
                                ]
                                """)));

        wireMockServer.stubFor(WireMock.get(urlEqualTo("/repos/abramovich-maks/Lotto/branches"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withFixedDelay(1000)
                        .withBody("""
                                [
                                  {
                                    "name": "main",
                                    "commit": { "sha": "12345" }
                                  }
                                ]
                                """)));

        wireMockServer.stubFor(WireMock.get(urlEqualTo("/repos/abramovich-maks/JobOffers/branches"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withFixedDelay(1000)
                        .withBody("""
                                [
                                  {
                                    "name": "main",
                                    "commit": { "sha": "12345" }
                                  }
                                ]
                                """)));


        mockMvc.perform(get("/api/github/abramovich-maks/repos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].repoName").value("Lotto"))
                .andExpect(jsonPath("$[1].repoName").value("JobOffers"))
                .andExpect(jsonPath("$[0].ownerLogin").value("abramovich-maks"))
                .andExpect(jsonPath("$[0].branches[0].name").value("main"))
                .andExpect(jsonPath("$[0].branches[0].commit.sha").value("12345"))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void should_return_404_when_user_not_found() throws Exception {
        wireMockServer.stubFor(WireMock.get(urlEqualTo("/users/unknown-user/repos"))
                .willReturn(aResponse().withStatus(404)));

        mockMvc.perform(get("/api/github/unknown-user/repos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("404 NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists());

    }

    @Test
    void should_fetch_branches_in_parallel_and_finish_in_around_2_seconds() throws Exception {
        wireMockServer.stubFor(WireMock.get(urlEqualTo("/users/abramovich-maks/repos"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withFixedDelay(1000)
                        .withBody("""
                                [
                                  {
                                    "name": "Lotto",
                                    "owner": { "login": "abramovich-maks" },
                                    "fork": false
                                  },
                                  {
                                    "name": "JobOffers",
                                    "owner": { "login": "abramovich-maks" },
                                    "fork": false
                                  },
                                  {
                                    "name": "ThisIsFork",
                                    "owner": { "login": "abramovich-maks" },
                                    "fork": true
                                  }
                                ]
                                """)));
        wireMockServer.stubFor(WireMock.get(urlEqualTo("/repos/abramovich-maks/Lotto/branches"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withFixedDelay(1000)
                        .withBody("""
                                [
                                  {
                                    "name": "main",
                                    "commit": { "sha": "12345" }
                                  }
                                ]
                                """)));

        wireMockServer.stubFor(WireMock.get(urlEqualTo("/repos/abramovich-maks/JobOffers/branches"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withFixedDelay(1000)
                        .withBody("""
                                [
                                  {
                                    "name": "main",
                                    "commit": { "sha": "12345" }
                                  }
                                ]
                                """)));

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        mockMvc.perform(get("/api/github/abramovich-maks/repos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        stopWatch.stop();
        long totalTimeMs = stopWatch.getTime();
        wireMockServer.verify(3, getRequestedFor(urlMatching(".*")));
        assertThat(totalTimeMs).isBetween(2000L, 2500L);
    }
}
