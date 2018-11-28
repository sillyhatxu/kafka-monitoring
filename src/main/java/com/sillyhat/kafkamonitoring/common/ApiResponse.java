package fashion.deja.common.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class DejaApiResponse {


    public static <T> DejaApiResponse.ApiResponseBuilder<T> builder() {
        return new DejaApiResponse.ApiResponseBuilder<>();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorMessage{
        private Integer errorType;
        private String title;
        private String description;
        private String contactUs;
    }

    public static class ApiResponseBuilder<T>{

        /**
         * default 200
         */
        private HttpStatus statusCode = HttpStatus.OK;

        private int ret = ApiStatus.OPERATOR_SUCCESS;

        private T data;

        private String msg = "success";
        private ErrorMessage errorMessage;

        public DejaApiResponse.ApiResponseBuilder<T> ret(int ret){
            this.ret = ret;
            return this;
        }

        public DejaApiResponse.ApiResponseBuilder<T> data(T data){
            this.data = data;
            return this;
        }

        public DejaApiResponse.ApiResponseBuilder<T> msg(String msg){
            this.msg = msg;
            return this;
        }

        public DejaApiResponse.ApiResponseBuilder<T> errorMessage(ErrorMessage errorMessage){
            this.errorMessage = errorMessage;
            return this;
        }

        public DejaApiResponse.ApiResponseBuilder<T> ok(){
            statusCode = HttpStatus.OK;
            return this;
        }

        public DejaApiResponse.ApiResponseBuilder<T> created(){
            statusCode = HttpStatus.CREATED;
            return this;
        }

        public DejaApiResponse.ApiResponseBuilder<T> unauthorized(){
            statusCode = HttpStatus.UNAUTHORIZED;
            setErrorMsg();
            return this;
        }

        public DejaApiResponse.ApiResponseBuilder<T> badRequest(){
            statusCode = HttpStatus.BAD_REQUEST;
            setErrorMsg();
            return this;
        }

        public DejaApiResponse.ApiResponseBuilder<T> notFound(){
            statusCode = HttpStatus.NOT_FOUND;
            setErrorMsg();
            return this;
        }

        public DejaApiResponse.ApiResponseBuilder<T> conflict(){
            statusCode = HttpStatus.CONFLICT;
            setErrorMsg();
            return this;
        }

        public DejaApiResponse.ApiResponseBuilder<T> internalServerError(){
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
            setErrorMsg();
            return this;
        }

        private void setErrorMsg(){
            this.ret = 0 - this.statusCode.value();
            this.msg = this.statusCode.getReasonPhrase();
        }

        public ResponseEntity<DejaApi<T>> build(){
            return new ResponseEntity<>(new DejaApi<>(ret,data,msg,errorMessage),statusCode);
        }
    }
}
