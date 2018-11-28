package com.sillyhat.kafkamonitoring.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Api<T> {

    private int ret;

    private T data;

    private String msg;

}