import Foundation

@objc public class AudioToggle: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
