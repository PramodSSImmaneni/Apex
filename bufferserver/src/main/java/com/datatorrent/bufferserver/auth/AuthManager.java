package com.datatorrent.bufferserver.auth;

import java.security.SecureRandom;

/**
 *
 */
public class AuthManager
{
  private final static int BUFFER_SERVER_TOKEN_LENGTH = 20;

  private static SecureRandom generator = new SecureRandom();

  public static byte[] generateToken()
  {
    byte[] token = new byte[BUFFER_SERVER_TOKEN_LENGTH];
    generator.nextBytes(token);
    return token;
  }
}
