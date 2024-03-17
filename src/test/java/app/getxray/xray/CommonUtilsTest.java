package app.getxray.xray;

import static app.getxray.xray.CommonUtils.isTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.Test;

import okhttp3.OkHttpClient;

class CommonUtilsTest {
 
    @Test
    void isTrueTruethnessTest() {
        assertAll("conditions for truethness", 
            () -> assertTrue(isTrue(Boolean.TRUE)),
            () -> assertTrue(isTrue(true))
        );
    }

    @Test
    void isTrueFalseTest() {
        assertAll("conditions for false", 
            () -> assertFalse(isTrue(Boolean.FALSE)),
            () -> assertFalse(isTrue(false)),
            () -> assertFalse(isTrue(null))
        );
    }

    @Test
    void getHttpClientTest() throws KeyManagementException, NoSuchAlgorithmException {
        OkHttpClient client = CommonUtils.getHttpClient(false, true, 5);
        assertNotNull(client);
    }

    @Test
    void getHttpClientUsingInternalProxyTest() throws KeyManagementException, NoSuchAlgorithmException {
        OkHttpClient client = CommonUtils.getHttpClient(true, true, 5);
        assertNotNull(client);
    } 

}
