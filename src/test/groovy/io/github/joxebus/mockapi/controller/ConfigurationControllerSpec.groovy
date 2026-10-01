package io.github.joxebus.mockapi.controller

import io.github.joxebus.mockapi.util.FileUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConfigurationControllerSpec extends Specification {

    @Autowired
    TestRestTemplate testRestTemplate

    @Value('${file.upload.folder}')
    private String fileUploadFolder

    def setup(){
        FileUtil.cleanFolder(fileUploadFolder)
    }

    def "Test create a configuration"() {
        given: "A configuration file in json format"
        String jsonApiConfig = FileUtil.getTextFromFile(fileName)

        HttpHeaders requestHeaders = new HttpHeaders()
        requestHeaders.setContentType(MediaType.APPLICATION_JSON)
        HttpEntity<String> data = new HttpEntity<>(jsonApiConfig, requestHeaders)

        when: "The configuration is created"
        def entity = testRestTemplate.postForEntity("/config", data, Map)

        then:
        entity.statusCode               == HttpStatus.CREATED
        entity.body.config              == configName
        entity.body.endpoints.size()    == numberOfEndpoints

        where:
        fileName                        |   configName              |   numberOfEndpoints
        "configuration_secured.json"    |   "/config/secured-api"   |   4
        "configuration_unsecured.json"  |   "/config/unsecured-api" |   4

    }

    def "Test create a configuration fails with bad request when invalid"() {
        given: "An invalid configuration payload"
        HttpHeaders requestHeaders = new HttpHeaders()
        requestHeaders.setContentType(MediaType.APPLICATION_JSON)
        HttpEntity<String> data = new HttpEntity<>(jsonApiConfig, requestHeaders)

        when: "The configuration is created"
        def entity = testRestTemplate.postForEntity("/config", data, Map)

        then:
        entity.statusCode == HttpStatus.BAD_REQUEST

        where:
        scenario                      | jsonApiConfig
        "missing name"                | '''{
            "paths": {
                "GLOSSARY": [
                    { "method": "get", "statusCode": 200, "body": "{}" }
                ]
            }
        }'''
        "blank name"                  | '''{
            "name": "   ",
            "paths": {
                "GLOSSARY": [
                    { "method": "get", "statusCode": 200, "body": "{}" }
                ]
            }
        }'''
        "empty paths"                 | '''{
            "name": "empty-paths-api",
            "paths": {}
        }'''
        "operation with empty list"   | '''{
            "name": "empty-op-api",
            "paths": {
                "GLOSSARY": []
            }
        }'''
        "path with blank method"      | '''{
            "name": "blank-method-api",
            "paths": {
                "GLOSSARY": [
                    { "method": "", "statusCode": 200, "body": "{}" }
                ]
            }
        }'''
        "path with invalid statusCode"| '''{
            "name": "bad-status-api",
            "paths": {
                "GLOSSARY": [
                    { "method": "get", "statusCode": 999, "body": "{}" }
                ]
            }
        }'''
        "secured without authConfig"  | '''{
            "name": "secured-no-auth-api",
            "secured": true,
            "paths": {
                "GLOSSARY": [
                    { "method": "get", "statusCode": 200, "body": "{}" }
                ]
            }
        }'''
        "duplicate method in operation"| '''{
            "name": "dup-method-api",
            "paths": {
                "GLOSSARY": [
                    { "method": "get", "statusCode": 200, "body": "{}" },
                    { "method": "get", "statusCode": 500, "body": "{}" }
                ]
            }
        }'''
        "duplicate method different case"| '''{
            "name": "dup-method-case-api",
            "paths": {
                "GLOSSARY": [
                    { "method": "get", "statusCode": 200, "body": "{}" },
                    { "method": "GET", "statusCode": 500, "body": "{}" }
                ]
            }
        }'''
    }

    def "Test config/apiName returns the API configuration available"() {
        given:
        String jsonApiConfig = FileUtil.getTextFromFile("configuration_secured.json")

        HttpHeaders requestHeaders = new HttpHeaders()
        requestHeaders.setContentType(MediaType.APPLICATION_JSON)
        HttpEntity<String> data = new HttpEntity<>(jsonApiConfig, requestHeaders)
        testRestTemplate.postForEntity("/config", data, Map)

        when:
        def entity = testRestTemplate.exchange("/config/secured-api", HttpMethod.GET, new HttpEntity<>(requestHeaders), Map)

        then:
        entity.statusCode == HttpStatus.OK
        entity.body.name == "secured-api"
        entity.body.description == "This is sample API to mock REST endpoint"
        entity.body.termsOfService == "http://example.com/terms/"
        entity.body.version == "1.0.1"
        entity.body.contact.name == "API Support"
        entity.body.contact.url == "http://www.example.com/support"
        entity.body.contact.email == "support@example.com"
        entity.body.license.name == "Apache 2.0"
        entity.body.license.url == "https://www.apache.org/licenses/LICENSE-2.0.html"
        entity.body.secured == true
        entity.body.authConfig == "am94ZWJ1czpNb2NrQVBJ"
        entity.body.paths.size() == 4
    }

    def "Verify config/apiName is not configured"() {
        given:

        HttpHeaders requestHeaders = new HttpHeaders()
        requestHeaders.setContentType(MediaType.APPLICATION_JSON)

        when:
        def entity = testRestTemplate.exchange("/config/secured-api", HttpMethod.GET, new HttpEntity<>(requestHeaders), Map)
        println entity

        then:
        entity.statusCode == HttpStatus.NOT_FOUND
        entity.body.message == "The configuration [secured-api] does not exist."

    }

    def "Test delete an existing configuration returns 200 and it is no longer available"() {
        given:
        String jsonApiConfig = FileUtil.getTextFromFile("configuration_secured.json")

        HttpHeaders requestHeaders = new HttpHeaders()
        requestHeaders.setContentType(MediaType.APPLICATION_JSON)
        HttpEntity<String> data = new HttpEntity<>(jsonApiConfig, requestHeaders)
        testRestTemplate.postForEntity("/config", data, Map)

        when:
        def entity = testRestTemplate.exchange("/config/secured-api", HttpMethod.DELETE, new HttpEntity<>(requestHeaders), Map)

        then:
        entity.statusCode == HttpStatus.OK
        entity.body.message == "The configuration [secured-api] was deleted."

        when:
        def getEntity = testRestTemplate.exchange("/config/secured-api", HttpMethod.GET, new HttpEntity<>(requestHeaders), Map)

        then:
        getEntity.statusCode == HttpStatus.NOT_FOUND
    }

    def "Test delete a non-existent configuration returns 404"() {
        given:
        HttpHeaders requestHeaders = new HttpHeaders()
        requestHeaders.setContentType(MediaType.APPLICATION_JSON)

        when:
        def entity = testRestTemplate.exchange("/config/secured-api", HttpMethod.DELETE, new HttpEntity<>(requestHeaders), Map)

        then:
        entity.statusCode == HttpStatus.NOT_FOUND
        entity.body.message == "The configuration [secured-api] does not exist."
    }

}
