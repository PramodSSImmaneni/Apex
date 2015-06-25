package com.datatorrent.bufferserver.client;

import com.datatorrent.netlet.AbstractLengthPrependerClient;

/**
 * Created by pramod on 6/25/15.
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

  protected void checkAuthenticate() {
    if (token != null) {
      write(token);
    }
  }

  public void setToken(byte[] token)
  {
    this.token = token;
  }
}
