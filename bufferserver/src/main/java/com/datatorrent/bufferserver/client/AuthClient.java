package com.datatorrent.bufferserver.client;

import com.datatorrent.netlet.AbstractLengthPrependerClient;

/**
 * This is a client that can be used for authentication between two peers.
 * Currently the authentication is one way from the initiator to the non-initiator.
 * It can be enhanced to do mutual authentication.
 *
 * @author Pramod Immaneni <pramod@datatorrent.com>
 */
public abstract class AuthClient extends AbstractLengthPrependerClient
{

  private byte[] token;
  protected boolean initiator;

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
    if ((token != null) && !initiator && !authenticated)
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

  @Override
  public boolean write(byte[] message1, byte[] message2)
  {
    checkAuthenticated();
    return super.write(message1, message2);
  }

  @Override
  public boolean write(byte[] message, int offset, int size)
  {
    checkAuthenticated();
    return super.write(message, offset, size);
  }

  private void checkAuthenticated()
  {
    if ((token != null) && initiator && !authenticated) {
      super.write(token, 0, token.length);
      authenticated = true;
    }
  }

  private void printHex(byte[] ba) {
    for (byte b : ba) {
      System.out.print(Integer.toHexString(b));
    }
  }

  protected abstract void onAuthMessage(byte[] buffer, int offset, int size);

  public byte[] getToken()
  {
    return token;
  }

  public void setToken(byte[] token)
  {
    this.token = token;
  }

  public boolean isInitiator()
  {
    return initiator;
  }

  public void setInitiator(boolean initiator)
  {
    this.initiator = initiator;
  }
}
