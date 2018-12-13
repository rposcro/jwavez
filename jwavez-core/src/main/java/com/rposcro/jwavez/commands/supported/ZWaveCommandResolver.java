package com.rposcro.jwavez.commands.supported;

import com.rposcro.jwavez.utils.ImmutableBuffer;

public interface ZWaveCommandResolver {

  ZWaveSupportedCommand resolve(ImmutableBuffer payloadBuffer);
}
