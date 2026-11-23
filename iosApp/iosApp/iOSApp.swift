import SwiftUI
import UserNotifications
import CoreSpotlight
import UniformTypeIdentifiers
import presentationKit

class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        UNUserNotificationCenter.current().delegate = self
        indexAppInSpotlight()
        return true
    }

    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        completionHandler([.banner, .sound, .badge])
    }

    private func indexAppInSpotlight() {
        let attributeSet = CSSearchableItemAttributeSet(contentType: UTType.item)
        attributeSet.title = "زاد المؤمن - ZadElMomen"
        attributeSet.contentDescription = "التطبيق الإسلامي الشامل: مواقيت الصلاة، القرآن الكريم، الأذكار، اتجاه القبلة، وراديو القرآن."
        attributeSet.keywords = ["ZadElMomen", "زاد المؤمن", "مواقيت الصلاة", "القرآن", "أذكار", "قبلة"]

        let searchableItem = CSSearchableItem(
            uniqueIdentifier: "dev.sayed.mehrabalmomen.main",
            domainIdentifier: "dev.sayed.mehrabalmomen.search",
            attributeSet: attributeSet
        )

        CSSearchableIndex.default().indexSearchableItems([searchableItem]) { error in
            if let error = error {
                print("Spotlight indexing error: \(error.localizedDescription)")
            }
        }
    }
}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
