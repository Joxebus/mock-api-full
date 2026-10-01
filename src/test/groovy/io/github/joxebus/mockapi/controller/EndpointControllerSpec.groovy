package io.github.joxebus.mockapi.controller


import io.github.joxebus.mockapi.util.FileUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EndpointControllerSpec extends Specification {

    @Autowired
    TestRestTemplate testRestTemplate

    @Value('${file.upload.folder}')
    private String fileUploadFolder

    def setup(){
        FileUtil.cleanFolder(fileUploadFolder)
    }

    def "Verify endpoints are created and can be reviewed"() {
        given:
        String jsonApiConfigSecured = FileUtil.getTextFromFile("configuration_secured.json")
        String jsonApiConfigUnsecured = FileUtil.getTextFromFile("configuration_unsecured.json")

        HttpHeaders requestHeaders = new HttpHeaders()
        requestHeaders.setContentType(MediaType.APPLICATION_JSON)
        HttpEntity<String> dataSecured = new HttpEntity<>(jsonApiConfigSecured, requestHeaders)
        HttpEntity<String> dataUnsecured = new HttpEntity<>(jsonApiConfigUnsecured, requestHeaders)
        testRestTemplate.postForEntity("/config", dataSecured, Map)
        testRestTemplate.postForEntity("/config", dataUnsecured, Map)

        when:
        def endpoints = testRestTemplate.exchange("/endpoint", HttpMethod.GET, new HttpEntity<>(requestHeaders), List)

        then:
        endpoints.statusCode == HttpStatus.OK
        endpoints.body.size() == 2
        endpoints.body[0].endpoints.size() == 4
        endpoints.body[1].endpoints.size() == 4

    }

    def "Verify paths of an endpoint configuration"() {
        given:
        String jsonApiConfigUnsecured = FileUtil.getTextFromFile("configuration_unsecured.json")

        HttpHeaders requestHeaders = new HttpHeaders()
        requestHeaders.setContentType(MediaType.APPLICATION_JSON)
        HttpEntity<String> dataUnsecured = new HttpEntity<>(jsonApiConfigUnsecured, requestHeaders)
        testRestTemplate.postForEntity("/config", dataUnsecured, Map)

        when:
        def apiPaths = testRestTemplate.exchange("/endpoint/unsecured-api", HttpMethod.GET, new HttpEntity<>(requestHeaders), Map)

        then:
        apiPaths.statusCode == HttpStatus.OK
        apiPaths.body.config == "/config/unsecured-api"
        apiPaths.body.endpoints.size() == 4
        apiPaths.body.endpoints*.href == ["/api/unsecured-api/GLOSSARY", "/api/unsecured-api/MENU", "/api/unsecured-api/CREATE_PERSON", "/api/unsecured-api/FAILURE"]
        apiPaths.body.endpoints*.operations*.method.flatten() == ["get", "put", "post", "delete"]
        apiPaths.body.endpoints*.operations*.statusCode.flatten() == [200, 202, 201, 500]


    }

    def "Verify throw error when no configuration found"() {
        given:
        HttpHeaders requestHeaders = new HttpHeaders()
        requestHeaders.setContentType(MediaType.APPLICATION_JSON)

        when:
        def apiPaths = testRestTemplate.exchange("/endpoint/unsecured-api", HttpMethod.GET, new HttpEntity<>(requestHeaders), Map)
        println apiPaths

        then:
        apiPaths.statusCode == HttpStatus.NOT_FOUND
        apiPaths.body.message == "The endpoint [unsecured-api] is not configured."



    }
}
