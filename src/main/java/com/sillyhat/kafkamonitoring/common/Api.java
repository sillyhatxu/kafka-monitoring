package fashion.deja.common.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DejaApi<T> {

    private int ret;

    private T data;

    private String msg;

    @JsonProperty("error_message")
    private DejaApiResponse.ErrorMessage errorMessage;

}