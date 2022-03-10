package com.k2archer.tomato.server.jmeter;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.testelement.property.JMeterProperty;

public class DefaultArguments {

    public static Arguments getArguments() {
        Arguments args = new Arguments();
        args.addArgument("path", "http://localhost:8080/api/user/login");
        args.addArgument("body", "{\"username\" : \"kwei\", \"password\" : \"123\" }");

        return args;
    }
}
