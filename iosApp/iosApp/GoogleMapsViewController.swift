import SwiftUI
import GoogleMaps

// MARK: - Google Maps View Controller
class GoogleMapsViewController: UIViewController {
    var mapView: GMSMapView!
    let latitude: Double = 52.3702  // Amsterdam
    let longitude: Double = 4.8952
    let zoomLevel: Float = 12.0

    override func viewDidLoad() {
        super.viewDidLoad()

        // Create a camera at the specified location
        let camera = GMSCameraPosition(latitude: latitude, longitude: longitude, zoom: zoomLevel)
        mapView = GMSMapView()
        mapView.frame = self.view.bounds
        mapView.camera = camera

        // Configure map settings
        mapView.settings.compassButton = true
        mapView.settings.myLocationButton = true
        mapView.settings.zoomGestures = true
        mapView.settings.scrollGestures = true
        mapView.settings.rotateGestures = true
        mapView.isMyLocationEnabled = true
        mapView.autoresizingMask = [.flexibleWidth, .flexibleHeight]

        // Add marker
        let marker = GMSMarker()
        marker.position = CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
        marker.title = "Amsterdam"
        marker.snippet = "Current Location"
        marker.map = mapView

        self.view.addSubview(mapView)
    }
}

// MARK: - UIViewControllerRepresentable for SwiftUI
struct GoogleMapsViewControllerRepresentable: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> GoogleMapsViewController {
        return GoogleMapsViewController()
    }

    func updateUIViewController(_ uiViewController: GoogleMapsViewController, context: Context) {
        // Update logic here if needed
    }
}

// MARK: - Convenience View for SwiftUI
struct GoogleMapsViewSwiftUI: View {
    var body: some View {
        GoogleMapsViewControllerRepresentable()
            .edgesIgnoringSafeArea(.all)
    }
}

