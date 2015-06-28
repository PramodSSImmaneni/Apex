package com.datatorrent.bufferserver.server;

import com.datatorrent.netlet.AbstractLengthPrependerClient;

/**
 * This is a client that can be used for authentication with a token.
 *
 * @author Pramod Immaneni <pramod@datatorrent.com>
 */
public abstract class AuthClient extends AbstractLengthPrependerClient
{

  private byte[] token;

  private boolean authenticated = false;

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

  @Override
  public void onMessage(byte[] buffer, int offset, int size)
  {
    if ((token != null) && !authenticated)
    {
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
    } else {
      onAuthMessage(buffer, offset, size);
    }
  }

  protected abstract void onAuthMessage(byte[] buffer, int offset, int size);

  public void setToken(byte[] token)
  {
    this.token = token;
  }

}
