import React, { useState, useEffect } from 'react';
import { RealtimeContext } from './realtime-context';

export const RealtimeProvider = ({ children }) => {
  const [connected, setConnected] = useState(false);

  useEffect(() => {
    // Realtime connection stub or WebSocket connection
    setConnected(true);
  }, []);

  return (
    <RealtimeContext.Provider value={{ connected }}>
      {children}
    </RealtimeContext.Provider>
  );
};
