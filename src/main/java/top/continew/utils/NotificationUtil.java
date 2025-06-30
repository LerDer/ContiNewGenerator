package top.continew.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;

public class NotificationUtil {

	// 使用 NotificationGroupManager 创建 NotificationGroup
	private static final NotificationGroup NOTIFICATION_GROUP =
			NotificationGroupManager.getInstance().getNotificationGroup("ContiNewGenerator.NotificationGroup");

	/**
	 * 显示错误信息
	 *
	 * @param project 当前项目
	 * @param title   通知标题
	 * @param message 通知内容
	 */
	public static void showErrorNotification(Project project, String title, String message) {
		if (project != null) {
			Notification notification = NOTIFICATION_GROUP.createNotification(
					title,  // 通知标题
					message,  // 通知内容
					NotificationType.ERROR  // 通知类型
			);
			notification.notify(project);  // 显示通知
		}
	}

	/**
	 * 显示警告信息
	 *
	 * @param project 当前项目
	 * @param title   通知标题
	 * @param message 通知内容
	 */
	public static void showWarningNotification(Project project, String title, String message) {
		if (project != null) {
			Notification notification = NOTIFICATION_GROUP.createNotification(
					title,
					message,
					NotificationType.WARNING
			);
			notification.notify(project);
		}
	}

	public static void showWarningNotification(String title, String message) {
		Notification notification = NOTIFICATION_GROUP.createNotification(
				title,
				message,
				NotificationType.WARNING
		);
		//此方法会将通知发送到全局通知总线。这意味着通知会在整个 IDE 范围内显示，不局限于特定项目。无论用户打开多少项目，通知都会展示
		Notifications.Bus.notify(notification);
		//该方法仅在指定项目内显示通知。若用户打开多个项目，通知只会在传入的 project 项目中出现。
		//notification.notify(project);
	}

	/**
	 * 显示普通信息
	 *
	 * @param project 当前项目
	 * @param title   通知标题
	 * @param message 通知内容
	 */
	public static void showInfoNotification(Project project, String title, String message) {
		if (project != null) {
			Notification notification = NOTIFICATION_GROUP.createNotification(
					title,
					message,
					NotificationType.INFORMATION
			);
			notification.notify(project);
		}
	}
}