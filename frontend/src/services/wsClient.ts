export type WSClient = {
  send: (data: any) => void;
  close: () => void;
  connected: () => boolean;
};

export function createWSClient(
  url: string,
  onMessage: (data: any) => void,
  onOpen?: () => void,
  onClose?: () => void
): WSClient {
  let ws: WebSocket | null = null;
  let heartbeatTimer: any = null;
  let reconnectTimer: any = null;
  let backoff = 1000; // start 1s
  const maxBackoff = 15000;
  const queue: any[] = [];

  const startHeartbeat = () => {
    stopHeartbeat();
    heartbeatTimer = setInterval(() => {
      try { ws?.send(JSON.stringify({ type: 'ping', t: Date.now() })); } catch {}
    }, 25000);
  };
  const stopHeartbeat = () => { if (heartbeatTimer) { clearInterval(heartbeatTimer); heartbeatTimer = null; } };

  const flushQueue = () => {
    if (!ws || ws.readyState !== WebSocket.OPEN) return;
    while (queue.length) {
      const payload = queue.shift();
      try { ws.send(JSON.stringify(payload)); } catch { break; }
    }
  };

  const connect = () => {
    try {
      ws = new WebSocket(url);
      ws.onopen = () => {
        backoff = 1000;
        startHeartbeat();
        flushQueue();
        onOpen && onOpen();
      };
      ws.onmessage = (evt) => {
        try { onMessage(JSON.parse(evt.data)); } catch { /* ignore */ }
      };
      ws.onclose = () => {
        stopHeartbeat();
        onClose && onClose();
        // schedule reconnect
        if (!reconnectTimer) {
          reconnectTimer = setTimeout(() => {
            reconnectTimer = null;
            backoff = Math.min(backoff * 2, maxBackoff);
            connect();
          }, backoff);
        }
      };
      ws.onerror = () => {
        try { ws && ws.close(); } catch {}
      };
    } catch {
      // retry later
      if (!reconnectTimer) {
        reconnectTimer = setTimeout(() => {
          reconnectTimer = null;
          backoff = Math.min(backoff * 2, maxBackoff);
          connect();
        }, backoff);
      }
    }
  };

  connect();

  return {
    send: (data: any) => {
      if (ws && ws.readyState === WebSocket.OPEN) {
        try { ws.send(JSON.stringify(data)); } catch { queue.push(data); }
      } else {
        queue.push(data);
      }
    },
    close: () => {
      stopHeartbeat();
      try { ws?.close(); } catch {}
    },
    connected: () => !!ws && ws.readyState === WebSocket.OPEN,
  };
}
