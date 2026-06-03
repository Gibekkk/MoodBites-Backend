package com.moodbites.restfulapi.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ImageController.class)
@AutoConfigureMockMvc(addFilters = false) // Bypass security filters
@TestPropertySource(properties = {
        "storage.api-prefix=/api" // Mensimulasikan variabel application.properties
})
@DisplayName("ImageController Web Layer Tests")
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final String BASE_URL = "/api/images/";
    private byte[] dummyImageBytes;

    @BeforeEach
    void setUp() {
        // Dummy data untuk merepresentasikan byte gambar JPEG
        dummyImageBytes = new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0 };
    }

    @Nested
    @DisplayName("GET /** (getImage)")
    class GetImage {

        @Test
        @DisplayName("Returns 200 OK with Image content and Cache-Control headers when file exists")
        void whenFileExists_thenReturnsImageAndCacheHeaders(@TempDir Path tempDir) throws Exception {
            // 1. Arrange: Buat file fisik sementara
            Path testFile = tempDir.resolve("test-image.jpeg");
            Files.write(testFile, dummyImageBytes);

            // Encode path aslinya ke Base64 (sesuai ekspektasi controller)
            String base64Path = Base64.getEncoder().encodeToString(testFile.toString().getBytes());

            // 2. Act & 3. Assert
            mockMvc.perform(get(BASE_URL + base64Path))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                    // CacheControl.maxAge(1, TimeUnit.DAYS).cachePublic() akan menghasilkan header di bawah
                    .andExpect(header().string(HttpHeaders.CACHE_CONTROL, "max-age=86400, public"))
                    .andExpect(content().bytes(dummyImageBytes));
        }

        @Test
        @DisplayName("Returns 404 NOT FOUND when Base64 is valid but file does not exist")
        void whenFileDoesNotExist_thenReturns404(@TempDir Path tempDir) throws Exception {
            // Path yang valid secara format, tapi filenya tidak pernah dibuat
            Path ghostFile = tempDir.resolve("ghost.jpeg");
            String base64Path = Base64.getEncoder().encodeToString(ghostFile.toString().getBytes());

            mockMvc.perform(get(BASE_URL + base64Path))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value("Image Not Found"));
        }

        @Test
        @DisplayName("Returns 400 BAD REQUEST when URL path is not a valid Base64 string")
        void whenInvalidBase64_thenReturns400() throws Exception {
            // Mengirim string yang bukan Base64 valid (mengandung karakter terlarang atau format salah)
            String invalidBase64 = "invalid_base64_string!@#";

            mockMvc.perform(get(BASE_URL + invalidBase64))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").exists()); // Error message dari IllegalArgumentException
        }

        @Test
        @DisplayName("Returns 500 INTERNAL SERVER ERROR for unexpected exceptions")
        void whenUnexpectedException_thenReturns500() throws Exception {
            // Jika request path lebih pendek dari apiEndpoint, substring() akan melempar StringIndexOutOfBoundsException
            // Karena kita melakukan setting BASE_URL sebagai "/api/images/", kita bisa memicu error 
            // ini dengan menembak mapping yang lolos secara routing, tapi stringnya tidak lengkap.
            // MockMvc me-mock getRequestURI(), kita bisa manipulasi via request attribute jika dibutuhkan.
            // Namun, memicu error ini secara native bisa dilakukan dengan mengirim URI yang sengaja di-set salah di MockHttpServletRequestBuilder.
            
            mockMvc.perform(get(BASE_URL + "validBase64")
                            .requestAttr("jakarta.servlet.include.request_uri", "/short")) // Memicu error parsing URL internal
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").exists());
        }
    }
}