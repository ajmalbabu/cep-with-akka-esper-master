package com.cep.service.common;

/**
 * Implement this interface if the test is integration in nature, which takes more time to execute.
 * so that these test cases are not run all the time, especially in developer laptop. These tests
 * are generally run by a build system.
 */
public interface IntegrationTest {
}
