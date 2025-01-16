#include <iostream>
#include <vector>
#include <string>
#include <fstream>
#include <sstream>
#include <iomanip>
#include <conio.h> // For password masking on Windows
using namespace std;

// Block structure
class Block {
private:
    int id;
    string projectName;
    double allocatedAmount;
    string deadline;
    string status;
    string city;
    string previousHash;
    string currentHash;

    string calculateHash() {
        return to_string(id) + projectName + to_string(allocatedAmount) +
               deadline + status + city + previousHash;
    }

public:
    Block(int id, const string& projectName, double allocatedAmount, const string& deadline,
          const string& status, const string& city, const string& previousHash)
        : id(id), projectName(projectName), allocatedAmount(allocatedAmount),
          deadline(deadline), status(status), city(city), previousHash(previousHash) {
        currentHash = calculateHash();
    }

    int getId() const { return id; }
    string getProjectName() const { return projectName; }
    double getAllocatedAmount() const { return allocatedAmount; }
    string getDeadline() const { return deadline; }
    string getStatus() const { return status; }
    string getCity() const { return city; }
    string getPreviousHash() const { return previousHash; }
    string getCurrentHash() const { return currentHash; }
};

// Blockchain structure
class Blockchain {
private:
    vector<Block> chain;
    const string filename = "blockchain_data.txt";

    void loadFromFile() {
        ifstream file(filename);
        if (!file.is_open()) {
            cout << "No existing blockchain found. Initializing new blockchain.\n";
            return;
        }

        string line;
        while (getline(file, line)) {
            stringstream ss(line);
            vector<string> fields;
            string token;

            while (getline(ss, token, ',')) {
                fields.push_back(token);
            }

            if (fields.size() != 7) continue;
            chain.emplace_back(stoi(fields[0]), fields[1], stod(fields[2]), fields[3], fields[4], fields[5], fields[6]);
        }
        file.close();
    }

    void saveToFile() {
        ofstream file(filename, ios::trunc);
        for (const auto& block : chain) {
            file << block.getId() << "," << block.getProjectName() << "," << block.getAllocatedAmount()
                 << "," << block.getDeadline() << "," << block.getStatus() << "," << block.getCity()
                 << "," << block.getPreviousHash() << "," << block.getCurrentHash() << endl;
        }
        file.close();
    }

    string getPassword() {
        string password;
        char ch;
        cout << "Enter Password: ";
        while ((ch = _getch()) != '\r') { // '\r' is Enter key
            if (ch == '\b' && !password.empty()) { // Backspace handling
                password.pop_back();
                cout << "\b \b";
            } else if (ch != '\b') {
                password.push_back(ch);
                cout << '*';
            }
        }
        cout << endl;
        return password;
    }

public:
    Blockchain() {
        loadFromFile();
        if (chain.empty()) {
            // Genesis block creation
            string genesisHash = "0";
            chain.emplace_back(0, "Genesis Block", 0.0, "N/A", "Static", "N/A", genesisHash);
            saveToFile();
        }
    }

    void addBlock(const string& projectName, double allocatedAmount, const string& deadline,
                  const string& status, const string& city) {
        string previousHash = chain.empty() ? "0" : chain.back().getCurrentHash();
        chain.emplace_back(chain.size(), projectName, allocatedAmount, deadline, status, city, previousHash);
        saveToFile();
    }

    void viewBlockchain() const {
        cout << "+-----+---------------------+------------+---------------+---------------+-----------------+-------------+-------------+\n";
        cout << "| ID  | Project Name        | Amount     | Deadline      | Status        | City            | Prev. Hash  | Curr. Hash  |\n";
        cout << "+-----+---------------------+------------+---------------+---------------+-----------------+-------------+-------------+\n";
        for (const auto& block : chain) {
            cout << "| " << setw(4) << block.getId()
                 << " | " << setw(19) << block.getProjectName()
                 << " | " << setw(10) << block.getAllocatedAmount()
                 << " | " << setw(13) << block.getDeadline()
                 << " | " << setw(13) << block.getStatus()
                 << " | " << setw(15) << block.getCity()
                 << " | " << setw(11) << block.getPreviousHash().substr(0, 6) // Truncated hashes
                 << " | " << setw(11) << block.getCurrentHash().substr(0, 6) << " |\n";
        }
        cout << "+-----+---------------------+------------+---------------+---------------+-----------------+-------------+-------------+\n";
    }

    void viewProjectsByCity() const {
        string city;
        cout << "Enter City: ";
        cin.ignore();
        getline(cin, city);

        bool found = false;
        cout << "\nProjects in " << city << ":\n";
        for (const auto& block : chain) {
            if (block.getCity() == city) {
                cout << "Project Name: " << block.getProjectName()
                     << ", Allocated Amount: " << block.getAllocatedAmount()
                     << ", Status: " << block.getStatus() << "\n";
                found = true;
            }
        }
        if (!found) {
            cout << "No projects found in " << city << ".\n";
        }
    }

    void viewRemainingFundsForProject() const {
        string projectName;
        cout << "Enter Project Name: ";
        cin.ignore();
        getline(cin, projectName);

        for (const auto& block : chain) {
            if (block.getProjectName() == projectName) {
                if (block.getStatus() == "Pending") {
                    cout << "Remaining funds for project '" << projectName << "': "
                         << block.getAllocatedAmount() << endl;
                } else if (block.getStatus() == "Completed") {
                    cout << "Remaining funds for project '" << projectName << "': 0\n";
                } else {
                    cout << "Remaining funds for project '" << projectName << "': "
                         << block.getAllocatedAmount() * 0.8 << " (arbitrary lesser amount)\n";
                }
                return;
            }
        }
        cout << "Project not found.\n";
    }

    void officerMenu() {
        string username, password;
        cout << "Enter Officer Username: ";
        cin >> username;
        password = getPassword();

        if (username == "admin" && password == "admin") {
            cin.ignore(); // Clear the input buffer
            string projectName, deadline, status, city;
            double allocatedAmount;

            cout << "Enter Project Name: ";
            getline(cin, projectName);
            cout << "Enter Allocated Amount: ";
            cin >> allocatedAmount;
            cin.ignore(); // Clear the input buffer
            cout << "Enter Deadline: ";
            getline(cin, deadline);
            cout << "Enter Status: ";
            getline(cin, status);
            cout << "Enter City: ";
            getline(cin, city);

            addBlock(projectName, allocatedAmount, deadline, status, city);
            cout << "Block added successfully.\n";
        } else {
            cout << "Invalid credentials.\n";
        }
    }
};

int main() {
    Blockchain blockchain;
    int choice;

    do {
        cout << "\nGovernment Fund Allocation using Blockchain\n";
        cout << "1. Add Block (Officers Only)\n";
        cout << "2. View Blockchain\n";
        cout << "3. View Remaining Funds for Project\n";
        cout << "4. View Projects by City (Public)\n";
        cout << "5. Exit\n";
        cout << "Enter your choice: ";
        cin >> choice;

        switch (choice) {
        case 1:
            blockchain.officerMenu();
            break;

        case 2:
            blockchain.viewBlockchain();
            break;

        case 3:
            blockchain.viewRemainingFundsForProject();
            break;

        case 4:
            blockchain.viewProjectsByCity();
            break;

        case 5:
            cout << "Exiting program.\n";
            break;

        default:
            cout << "Invalid choice. Try again.\n";
        }
    } while (choice != 5);

    return 0;
}
