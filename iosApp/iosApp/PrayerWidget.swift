import WidgetKit
import SwiftUI

struct PrayerInfo: Identifiable {
    let id = UUID()
    let name: String
    let time: String
    let isNext: Bool
}

struct Provider: TimelineProvider {
    func placeholder(in context: Context) -> SimpleEntry {
        SimpleEntry(
            date: Date(),
            hijriDate: "1 ذو القعدة",
            nextPrayer: "الفجر",
            nextPrayerTime: "04:30 ص",
            prayers: [
                PrayerInfo(name: "الفجر", time: "04:30 ص", isNext: true),
                PrayerInfo(name: "الظهر", time: "12:00 م", isNext: false),
                PrayerInfo(name: "العصر", time: "03:30 م", isNext: false),
                PrayerInfo(name: "المغرب", time: "06:15 م", isNext: false),
                PrayerInfo(name: "العشاء", time: "07:45 م", isNext: false)
            ]
        )
    }

    func getSnapshot(in context: Context, completion: @escaping (SimpleEntry) -> ()) {
        completion(placeholder(in: context))
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<SimpleEntry>) -> ()) {
        let currentDate = Date()

        let sharedDefaults = UserDefaults(suiteName: "group.dev.sayed.mehrabalmomen")
        let hijri = sharedDefaults?.string(forKey: "widget_hijri_date") ?? "1 ذو القعدة"
        let nextP = sharedDefaults?.string(forKey: "widget_next_prayer") ?? "الفجر"
        let nextT = sharedDefaults?.string(forKey: "widget_next_time") ?? "04:30 ص"

        let entry = SimpleEntry(
            date: currentDate,
            hijriDate: hijri,
            nextPrayer: nextP,
            nextPrayerTime: nextT,
            prayers: [
                PrayerInfo(name: "الفجر", time: sharedDefaults?.string(forKey: "p_fajr") ?? "04:30 ص", isNext: nextP == "الفجر"),
                PrayerInfo(name: "الظهر", time: sharedDefaults?.string(forKey: "p_dhuhr") ?? "12:00 م", isNext: nextP == "الظهر"),
                PrayerInfo(name: "العصر", time: sharedDefaults?.string(forKey: "p_asr") ?? "03:30 م", isNext: nextP == "العصر"),
                PrayerInfo(name: "المغرب", time: sharedDefaults?.string(forKey: "p_maghrib") ?? "06:15 م", isNext: nextP == "المغرب"),
                PrayerInfo(name: "العشاء", time: sharedDefaults?.string(forKey: "p_isha") ?? "07:45 م", isNext: nextP == "العشاء")
            ]
        )

        let nextUpdate = Calendar.current.date(byAdding: .minute, value: 30, to: currentDate)!
        let timeline = Timeline(entries: [entry], policy: .after(nextUpdate))
        completion(timeline)
    }
}

struct SimpleEntry: TimelineEntry {
    let date: Date
    let hijriDate: String
    let nextPrayer: String
    let nextPrayerTime: String
    let prayers: [PrayerInfo]
}

struct PrayerWidgetEntryView : View {
    var entry: Provider.Entry

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            HStack {
                Image(systemName: "moon.stars.fill")
                    .foregroundColor(.orange)
                    .font(.caption)
                Text(entry.hijriDate)
                    .font(.caption)
                    .bold()
                    .foregroundColor(.secondary)
                Spacer()
                Text("المقبل: \(entry.nextPrayer)")
                    .font(.caption2)
                    .foregroundColor(.orange)
            }

            Divider()

            HStack(spacing: 8) {
                ForEach(entry.prayers) { prayer in
                    VStack(spacing: 2) {
                        Text(prayer.name)
                            .font(.system(size: 10))
                            .foregroundColor(prayer.isNext ? .orange : .secondary)
                        Text(prayer.time)
                            .font(.system(size: 11, weight: prayer.isNext ? .bold : .regular))
                            .foregroundColor(prayer.isNext ? .primary : .secondary)
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 4)
                    .background(prayer.isNext ? Color.orange.opacity(0.15) : Color.clear)
                    .cornerRadius(6)
                }
            }
        }
        .padding(8)
        .containerBackground(.fill.tertiary, for: .widget)
    }
}

@main
struct PrayerWidget: Widget {
    let kind: String = "PrayerWidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: Provider()) { entry in
            PrayerWidgetEntryView(entry: entry)
        }
        .configurationDisplayName("مواقيت الصلاة")
        .description("عرض مواقيت الصلاة اليومية والتاريخ الهجري على شاشة الآيفون.")
        .supportedFamilies([.systemMedium])
    }
}
