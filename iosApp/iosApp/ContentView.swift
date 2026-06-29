import UIKit
import SwiftUI
import Shared
import GoogleMaps

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Self.Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Self.Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea()
            .onAppear {
                // Initialize Google Maps with API key
                GMSServices.provideAPIKey("AIzaSyCurf0k1ZnYq6PmqEbUrObjuT2HD0a6hL8")
            }
    }
}

