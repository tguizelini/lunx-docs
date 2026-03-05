
import SwiftUI

// MARK: - Data Models
struct UserProfile: Codable, Identifiable {
    let id: String
    let firstName: String
    let lastName: String
    let email: String
    let phoneNumber: String
    let jobTitle: String
    let profileImageURL: String?
    
    var fullName: String {
        if firstName.isEmpty && lastName.isEmpty {
            return ""
        }
        return "\(firstName) \(lastName)".trimmingCharacters(in: .whitespaces)
    }
    
    init(from response: UserProfileResponse){
        self.id = response.id
        self.firstName = response.firstName
        self.lastName = response.lastName
        self.email = response.email
        self.phoneNumber = response.phoneNumber
        self.jobTitle = response.jobTitle
        self.profileImageURL = response.profileImageURL
    }
}

struct Skill: Codable, Identifiable {
    let id: String
    let name: String
    let category: String
    let proficiencyLevel: Int
    let yearsOfExperience: Int
}

struct Experience: Codable, Identifiable {
    let id: String
    let company: String
    let position: String
    let startDate: String
    let endDate: String?
    let description: String
    let technologies: [String]?
    let achievements: [String]?
}

// TODO: Create UserProfileResponse struct to decode JSON
struct UserProfileResponse: Codable, Identifiable {
    let id: String
    let firstName: String
    let lastName: String
    let email: String
    let phoneNumber: String
    let jobTitle: String
    let profileImageURL: String?
    let skillset: [Skill]?
    let jobExperience: [Experience]?
}
// MARK: - Data Manager
class ProfileDataManager: ObservableObject {
    @Published var userProfile: UserProfile?
    @Published var skills: [Skill] = []
    @Published var experiences: [Experience] = []
    
    init() {
        loadDataFromJSON()
    }
    
    func loadDataFromJSON() {
        do{
            if let url = Bundle.main.url(forResource: "user_profile_json", withExtension: "json"), let data = try? Data(contentsOf: url) {
                let decoder = JSONDecoder()
                let userProfileResponse: UserProfileResponse = try decoder.decode(UserProfileResponse.self, from: data)
                
                userProfile = UserProfile(from: userProfileResponse)
                skills = userProfileResponse.skillset ?? []
                experiences = userProfileResponse.jobExperience ?? []
            }
        }catch{
            print("error")
        }
        
    }
    
}

// MARK: - Main Content View
struct ContentView: View {
    var body: some View {
        NavigationView {
            LoginView()
        }
    }
}

// MARK: - Login View
struct LoginView: View {
    @State private var isPressed = false
    
    var body: some View {
        VStack(spacing: 40) {
            Spacer()
            
            // App Logo
            Circle()
                .fill(LinearGradient(gradient: Gradient(colors: [Color.blue, Color.blue.opacity(0.7)]), startPoint: .topLeading, endPoint: .bottomTrailing))
                .frame(width: 120, height: 120)
                .overlay(
                    Image(systemName: "person.circle.fill")
                        .font(.system(size: 60))
                        .foregroundColor(.white)
                )
                .shadow(color: .blue.opacity(0.3), radius: 10, x: 0, y: 5)
            
            // Title
            VStack(spacing: 8) {
                Text("Profile App")
                    .font(.largeTitle)
                    .fontWeight(.bold)
                
                Text("Manage your professional profile")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)
            }
            
            Spacer()
            
            // Login Button
            NavigationLink(destination: ProfileView()) {
                HStack {
                    Image(systemName: "person.fill")
                        .font(.system(size: 18))
                    Text("Login")
                        .font(.headline)
                        .fontWeight(.semibold)
                }
                .foregroundColor(.white)
                .frame(maxWidth: .infinity)
                .frame(height: 56)
                .background(
                    LinearGradient(
                        gradient: Gradient(colors: [Color.blue, Color.blue.opacity(0.8)]),
                        startPoint: .leading,
                        endPoint: .trailing
                    )
                )
                .cornerRadius(28)
                .shadow(color: .blue.opacity(0.3), radius: 8, x: 0, y: 4)
                .scaleEffect(isPressed ? 0.95 : 1.0)
            }
            .buttonStyle(PlainButtonStyle())
            .accessibilityIdentifier("Login")
            .simultaneousGesture(
                DragGesture(minimumDistance: 0)
                    .onChanged { _ in
                        withAnimation(.easeInOut(duration: 0.1)) {
                            isPressed = true
                        }
                    }
                    .onEnded { _ in
                        withAnimation(.easeInOut(duration: 0.1)) {
                            isPressed = false
                        }
                    }
            )
            .padding(.horizontal, 40)
            
            Spacer()
            
            // Footer
            VStack(spacing: 4) {
                Text("Demo App")
                    .font(.caption)
                    .foregroundColor(.secondary)
                Text("Built with SwiftUI")
                    .font(.caption2)
                    .foregroundColor(.secondary)
            }
            .padding(.bottom, 20)
        }
        .navigationBarHidden(true)
        .background(
            LinearGradient(
                gradient: Gradient(colors: [Color(.systemBackground), Color(.systemGray6)]),
                startPoint: .top,
                endPoint: .bottom
            )
        )
    }
}

// MARK: - Profile View
struct ProfileView: View {
    @StateObject private var dataManager: ProfileDataManager = ProfileDataManager()
    @State private var showingChangePassword = false
    @State private var showingSignOutAlert = false
    
    var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                // TODO: Implement profile data display with conditional binding
                
                
                // Header Section - TODO: Connect to real JSON data
                VStack(spacing: 16) {
                    // Profile Image
                    Circle()
                        .fill(Color.blue)
                        .frame(width: 120, height: 120)
                        .overlay(
                            Group {
                                if let link = dataManager.userProfile?.profileImageURL {
                                    
                                }else{
                                    Image(systemName: "person.fill")
                                        .font(.system(size: 50))
                                        .foregroundColor(.white)
                                }
                            }
                            
                        )
                    
                    // TODO: Replace with actual user data from JSON when dataManager.userProfile is available
                    VStack(spacing: 4) {
                        Text(dataManager.userProfile?.fullName ?? "")
                            .font(.title2)
                            .fontWeight(.semibold)
                            .accessibilityIdentifier("Alex Doe")
                        
                        Text(dataManager.userProfile?.jobTitle ?? "") 
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                            .accessibilityIdentifier("iOS Developer")
                    }
                }
                .padding(.top, 20)
                
                // TODO: Implement Profile Information Section using JSON data
                profileInformationSection
                
                
                // TODO: Implement Skills Section using JSON data
                skillsSection
                // TODO: Implement Experience Section using JSON data
                experienceSection
                
            }
        }
        .accessibilityIdentifier("profileScrollView")
        .navigationTitle("Profile")
        .navigationBarTitleDisplayMode(.inline)
        .alert("Change Password", isPresented: $showingChangePassword) {
            Button("Cancel", role: .cancel) { }
            Button("Confirm") { }
        } message: {
            Text("This feature is not implemented in this demo.")
        }
        .alert("Sign Out", isPresented: $showingSignOutAlert) {
            Button("Cancel", role: .cancel) { }
            Button("Sign Out", role: .destructive) { }
        } message: {
            Text("Are you sure you want to sign out?")
        }
    }
    
    private var profileInformationSection: some View {
        VStack(spacing: 8){
            ProfileField(label: "FIRST NAME", value: dataManager.userProfile?.firstName ?? "", isLink: false, icon: nil)
            
            ProfileField(label: "LAST NAME", value: dataManager.userProfile?.lastName ?? "", isLink: false, icon: nil)
            
            ProfileField(label: "EMAIL", value: dataManager.userProfile?.email ?? "", isLink: true, icon: nil)
            
            ProfileField(label: "PHONE NUMBER", value: dataManager.userProfile?.phoneNumber ?? "", isLink: false, icon: "phone")
        }
    }
    
    private var skillsSection: some View {
        let columns = [
            GridItem(.flexible()),
            GridItem(.flexible())
        ]
        
        return VStack(alignment: .leading){
            Text("Skills")
                .font(.headline)
                .foregroundColor(.secondary)
                .fontWeight(.medium)
                .accessibilityIdentifier("Skills")
            .padding(.horizontal, 16)
            
            LazyVGrid(columns: columns, spacing: 16) {
                
                ForEach(dataManager.skills) { skill in 
                    SkillCard(skill: skill)
                    
                }
            }.padding(.horizontal, 8)
        }
    }
    
    private var experienceSection: some View {
        
        return VStack(alignment: .leading){
            Text("Experience")
                .font(.headline)
                .foregroundColor(.secondary)
                .fontWeight(.medium)
                .accessibilityIdentifier("Experience")
                        .padding(.horizontal, 16)
            ForEach(dataManager.experiences) { experience in 
                ExperienceCard(experience: experience)                        
            }
            
        }
        
    }
        
       
}

// MARK: - Supporting Views (Provided - No changes needed)
struct ProfileField: View {
    let label: String
    let value: String
    var isLink: Bool = false
    var icon: String? = nil
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(label)
                .font(.caption)
                .foregroundColor(.secondary)
                .fontWeight(.medium)
                .accessibilityIdentifier(label)
            
            HStack {
                if let icon = icon {
                    Image(systemName: icon)
                        .foregroundColor(.secondary)
                        .frame(width: 16)
                }
                
                Text(value)
                    .font(.body)
                    .foregroundColor(isLink ? .blue : .primary)
                    .accessibilityIdentifier(value)
                
                Spacer()
            }
        }
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(12)
    }
}

struct SkillCard: View {
    let skill: Skill
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(skill.name)
                .font(.subheadline)
                .fontWeight(.medium)
                .accessibilityIdentifier(skill.name)
            
            Text(skill.category)
                .font(.caption)
                .foregroundColor(.secondary)
                .accessibilityIdentifier(skill.category)
            
            HStack {
                ForEach(1...5, id: \.self) { index in
                    Circle()
                        .fill(index <= skill.proficiencyLevel ? Color.blue : Color.gray.opacity(0.3))
                        .frame(width: 8, height: 8)
                }
                
                Spacer()
                
                Text("\(skill.yearsOfExperience)y")
                    .font(.caption2)
                    .foregroundColor(.secondary)
            }
        }
        .padding(12)
        .background(Color(.systemGray6))
        .cornerRadius(12)
    }
}

struct ExperienceCard: View {
    let experience: Experience
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text(experience.position)
                        .font(.headline)
                        .accessibilityIdentifier(experience.position)
                    
                    Text(experience.company)
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                        .accessibilityIdentifier(experience.company)
                }
                
                Spacer()
                
                Text(experienceDateRange)
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .accessibilityIdentifier(experienceDateRange)
            }
            
            Text(experience.description)
                .font(.body)
                .fixedSize(horizontal: false, vertical: true)
        }
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(12)
    }
    
    private var experienceDateRange: String {
        let endDate = experience.endDate ?? "Present"
        return "\(experience.startDate) - \(endDate)"
    }
}

// MARK: - Button Styles (Provided - No changes needed)
struct SecondaryButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .font(.body)
            .fontWeight(.medium)
            .foregroundColor(.primary)
            .frame(maxWidth: .infinity)
            .padding()
            .background(Color(.systemGray6))
            .cornerRadius(12)
            .scaleEffect(configuration.isPressed ? 0.95 : 1.0)
    }
}

struct DangerButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .font(.body)
            .fontWeight(.medium)
            .foregroundColor(.red)
            .frame(maxWidth: .infinity)
            .padding()
            .background(Color(.systemGray6))
            .cornerRadius(12)
            .scaleEffect(configuration.isPressed ? 0.95 : 1.0)
    }
}

struct ProfileApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

#Preview {
    ContentView()
}
