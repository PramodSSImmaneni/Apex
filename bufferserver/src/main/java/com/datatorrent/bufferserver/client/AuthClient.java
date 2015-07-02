package com.datatorrent.bufferserver.client;

import com.datatorrent.netlet.AbstractLengthPrependerClient;

/**
 * 
 */
public abstract class AuthClient extends AbstractLengthPrependerClient
{
  private byte[] token;

  public AuthClient()
  {
  }

  public AuthClient(int readBufferSize, int sendBufferSize)
  {
    super(readBufferSize, sendBufferSize);
  }

  public AuthClient(byte[] readbuffer, int position, int sendBufferSize)
  {
    super(readbuffer, position, sendBufferSize);
  }

  protected void sendAuthenticate() {
    if (token != null) {
      write(token);
    }
  }

  protected void authenticateMessage(byte[] buffer, int offset, int size)
  {
    if (token != null) {
      boolean authenticated = false;
      if (size == token.length) {
        int match = 0;
        while ((match < token.length) && (buffer[offset + match] == token[match])) {
          ++match;
        }
        if (match == token.length) {
          authenticated = true;
        }
      }
      if (!authenticated) {
        throw new RuntimeException("Authentication failure");
      }
    }
  }

  public void setToken(byte[] token)
  {
    this.token = token;
  }
}
