package com.sillyhat.kafkamonitoring.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponse {


    public static <T> ApiResponse.ApiResponseBuilder<T> builder() {
        return new ApiResponse.ApiResponseBuilder<>();
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


        public ApiResponse.ApiResponseBuilder<T> ret(int ret){
            this.ret = ret;
            return this;
        }

        public ApiResponse.ApiResponseBuilder<T> data(T data){
            this.data = data;
            return this;
        }

        public ApiResponse.ApiResponseBuilder<T> msg(String msg){
            this.msg = msg;
            return this;
        }

        public ApiResponse.ApiResponseBuilder<T> ok(){
            statusCode = HttpStatus.OK;
            return this;
        }

        public ApiResponse.ApiResponseBuilder<T> created(){
            statusCode = HttpStatus.CREATED;
            return this;
        }

        public ApiResponse.ApiResponseBuilder<T> unauthorized(){
            statusCode = HttpStatus.UNAUTHORIZED;
            setErrorMsg();
            return this;
        }

        public ApiResponse.ApiResponseBuilder<T> badRequest(){
            statusCode = HttpStatus.BAD_REQUEST;
            setErrorMsg();
            return this;
        }

        public ApiResponse.ApiResponseBuilder<T> notFound(){
            statusCode = HttpStatus.NOT_FOUND;
            setErrorMsg();
            return this;
        }

        public ApiResponse.ApiResponseBuilder<T> conflict(){
            statusCode = HttpStatus.CONFLICT;
            setErrorMsg();
            return this;
        }

        public ApiResponse.ApiResponseBuilder<T> internalServerError(){
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
            setErrorMsg();
            return this;
        }

        private void setErrorMsg(){
            this.ret = 0 - this.statusCode.value();
            this.msg = this.statusCode.getReasonPhrase();
        }

        public ResponseEntity<Api<T>> build(){
            return new ResponseEntity<>(new Api<>(ret,data,msg),statusCode);
        }
    }
}
