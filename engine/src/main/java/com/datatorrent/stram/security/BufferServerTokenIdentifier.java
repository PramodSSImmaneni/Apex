package com.datatorrent.stram.security;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenIdentifier;

/**
 * Created by pramod on 5/27/15.
 */
public class BufferServerTokenIdentifier extends AbstractDelegationTokenIdentifier
{
  public static final Text IDENTIFIER_KIND = new Text("DT_BUFFSERV_TOKEN");

  public BufferServerTokenIdentifier()
  {
  }

  public BufferServerTokenIdentifier(Text owner, Text renewer, Text realUser)
  {
    super(owner, renewer, realUser);
  }

  @Override
  public Text getKind()
  {
    return IDENTIFIER_KIND;
  }
}
