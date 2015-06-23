package com.datatorrent.stram.security;

import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenSecretManager;

/**
 * Created by pramod on 5/27/15.
 */
public class BufferServerTokenManager extends AbstractDelegationTokenSecretManager<BufferServerTokenIdentifier>
{
  public BufferServerTokenManager(long delegationKeyUpdateInterval, long delegationTokenMaxLifetime, long delegationTokenRenewInterval, long delegationTokenRemoverScanInterval)
  {
    super(delegationKeyUpdateInterval, delegationTokenMaxLifetime, delegationTokenRenewInterval, delegationTokenRemoverScanInterval);
  }

  @Override
  public BufferServerTokenIdentifier createIdentifier()
  {
    return new BufferServerTokenIdentifier();
  }
}

