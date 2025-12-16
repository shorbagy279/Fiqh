import React from 'react';
import { WifiOff, Wifi } from 'lucide-react';
import { useNetworkStatus } from '../../hooks/useNetworkStatus';

const OfflineBanner = () => {
  const { isOnline, wasOffline } = useNetworkStatus();
  const [showReconnected, setShowReconnected] = React.useState(false);

  React.useEffect(() => {
    if (isOnline && wasOffline) {
      setShowReconnected(true);
      const timer = setTimeout(() => setShowReconnected(false), 3000);
      return () => clearTimeout(timer);
    }
  }, [isOnline, wasOffline]);

  if (isOnline && !showReconnected) return null;

  return (
    <div 
      className={`fixed top-0 left-0 right-0 py-2 px-4 flex items-center justify-center gap-2 z-50 shadow-lg transition-all ${
        isOnline 
          ? 'bg-green-600 text-white' 
          : 'bg-red-600 text-white'
      }`}
    >
      {isOnline ? <Wifi size={20} /> : <WifiOff size={20} />}
      <span className="text-sm font-bold">
        {isOnline ? 'تم استعادة الاتصال بالإنترنت' : 'لا يوجد اتصال بالإنترنت'}
      </span>
    </div>
  );
};

export default OfflineBanner;
