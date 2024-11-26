package se.tristanfarkas.forza.api.forsete;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In-order to mimic the requests the browser makes, we need to be able to perform certain types
 * of requests.
 */
public class ForseteForm {
    private Map<String, String> formData;
    private Map<String, ForseteFile> fileData;
    private List<String> emptyFileData;

    private ForseteForm(Builder b) {
        this.formData = b.currentData;
        this.fileData = b.fileData;
        this.emptyFileData = b.emptyFileData;
    }

    public static class Builder {
        private Map<String, String> currentData = new HashMap<>();
        private Map<String, ForseteFile> fileData = new HashMap<>();
        private List<String> emptyFileData = new ArrayList<>();

        Builder add(String k, String v) {
            currentData.put(k, v);
            return this;
        }

        Builder addFile(String k, ForseteFile v) {
            fileData.put(k, v);
            return this;
        }

        Builder emptyFile(String k) {
            emptyFileData.add(k);
            return this;
        }

        ForseteForm build() {
            return new ForseteForm(this);
        }
    }

    MultipartBody getMultipartBody() {
        var multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        formData.forEach(multipartBodyBuilder::addFormDataPart);
        fileData.forEach((k, v) -> {
            var body = RequestBody.create(okhttp3.MediaType.parse("application/octet-stream"), v.fileContent());
            multipartBodyBuilder.addFormDataPart(k, v.fileName(), body);
        });
        emptyFileData.forEach((k) -> {
            var body = RequestBody.create(new byte[0], okhttp3.MediaType.parse("application/octet-stream"));
            multipartBodyBuilder.addFormDataPart(k, "", body);
        });
        return multipartBodyBuilder.build();
    }
}
