class NotificationService {
  constructor() {
    this.permission = Notification.permission;
  }

  // Request notification permission
  async requestPermission() {
    if (!('Notification' in window)) {
      console.log('This browser does not support notifications');
      return false;
    }

    if (this.permission === 'granted') {
      return true;
    }

    if (this.permission !== 'denied') {
      const permission = await Notification.requestPermission();
      this.permission = permission;
      return permission === 'granted';
    }

    return false;
  }

  // Show notification
  showNotification(title, options = {}) {
    if (this.permission !== 'granted') {
      return;
    }

    const defaultOptions = {
      icon: '/vite.svg',
      badge: '/vite.svg',
      vibrate: [200, 100, 200],
      requireInteraction: false,
      ...options
    };

    new Notification(title, defaultOptions);
  }

  // Check if user should be reminded
  shouldRemind(user) {
    if (!user?.dailyReminders) {
      return false;
    }

    const lastActivityDate = user.lastActivityDate 
      ? new Date(user.lastActivityDate) 
      : null;
    
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    // If no last activity or last activity was not today
    if (!lastActivityDate) {
      return true;
    }

    const lastActivityDay = new Date(lastActivityDate);
    lastActivityDay.setHours(0, 0, 0, 0);

    return lastActivityDay < today;
  }

  // Send daily reminder
  sendDailyReminder(user) {
    if (!this.shouldRemind(user)) {
      return;
    }

    const currentHour = new Date().getHours();
    
    // Only send reminder between 9 AM and 9 PM
    if (currentHour < 9 || currentHour > 21) {
      return;
    }

    // Check if we already sent reminder today
    const lastReminderDate = localStorage.getItem('lastReminderDate');
    const today = new Date().toDateString();

    if (lastReminderDate === today) {
      return;
    }

    this.showNotification('🕌 تذكير يومي - فقه ماستر', {
      body: `مرحباً ${user.fullName}! حان وقت اختبارك اليومي. حافظ على سلسلتك! 🔥`,
      tag: 'daily-reminder',
      data: { type: 'daily-reminder' }
    });

    localStorage.setItem('lastReminderDate', today);
  }

  // Schedule reminder check
  scheduleReminderCheck(user) {
    // Check immediately
    this.sendDailyReminder(user);

    // Then check every hour
    setInterval(() => {
      this.sendDailyReminder(user);
    }, 60 * 60 * 1000); // 1 hour
  }
}

const notificationService = new NotificationService();
export default notificationService;