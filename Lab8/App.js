// COMP3074 - React Native Lab: REST API, FlatList, and User Details
// Travis Eweka - 101583426
//
// The whole app lives in this one file, the way the handout asks for.
// It fetches 10 users from the JSONPlaceholder REST API, lists them with
// FlatList, and shows one user's full details when a row is pressed.
//
// Optional challenges done: search field (filters by name), circular
// initials avatar, Refresh button, and a Retry button when the request fails.

import React, { useCallback, useEffect, useState } from 'react';
import {
  ActivityIndicator,
  FlatList,
  Platform,
  Pressable,
  SafeAreaView,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';

const API_URL = 'https://jsonplaceholder.typicode.com/users';

// One place for every colour, so the list screen and the details screen
// always match. Same warm-on-dark idea as my Lab 7 card.
const COLORS = {
  background: '#0F1115',
  card: '#1A1D25',
  cardSoft: '#222631',
  accent: '#F4A261',
  accentDeep: '#E76F51',
  teal: '#2A9D8F',
  heading: '#F5F3EF',
  body: '#D7DAE0',
  muted: '#8E95A1',
  error: '#E5484D',
  onAccent: '#0F1115',
};

// Builds the two letters shown in the round avatar, e.g. "Leanne Graham" -> "LG".
function getInitials(fullName) {
  const words = String(fullName || '').trim().split(/\s+/);
  const first = words[0] ? words[0][0] : '?';
  const last = words.length > 1 ? words[words.length - 1][0] : '';
  return (first + last).toUpperCase();
}

// One row of the list. It gets a single user plus the function to run when
// it is pressed. Pressable is what makes the row selectable.
function UserItem({ user, onPress }) {
  return (
    <Pressable
      onPress={onPress}
      // The style can be a function, which lets the row dim while held down.
      style={({ pressed }) => [styles.userItem, pressed && styles.userItemPressed]}
    >
      <View style={styles.avatar}>
        <Text style={styles.avatarText}>{getInitials(user.name)}</Text>
      </View>

      <View style={styles.userItemText}>
        <Text style={styles.userName}>{user.name}</Text>
        <Text style={styles.userEmail}>{user.email}</Text>
      </View>

      <Text style={styles.chevron}>›</Text>
    </Pressable>
  );
}

// A label and a value stacked together, used all over the details screen.
function DetailRow({ label, value }) {
  return (
    <View style={styles.detailRow}>
      <Text style={styles.detailLabel}>{label}</Text>
      <Text style={styles.detailValue}>{value}</Text>
    </View>
  );
}

export default function App() {
  const [users, setUsers] = useState([]); // everything the API sent back
  const [selectedUser, setSelectedUser] = useState(null); // null = show the list
  const [loading, setLoading] = useState(true); // show the spinner
  const [error, setError] = useState(null); // a message if the request failed
  const [search, setSearch] = useState(''); // what is typed in the search box

  // useCallback keeps one copy of this function around so the Refresh and
  // Retry buttons can call exactly the same loading code as the first load.
  const loadUsers = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await fetch(API_URL);
      if (!response.ok) {
        throw new Error(`HTTP error: ${response.status}`);
      }
      const data = await response.json();
      setUsers(data);
    } catch (err) {
      setError('Unable to load users. Check your connection and try again.');
    } finally {
      setLoading(false);
    }
  }, []);

  // The empty [] means this runs once, just after the first render.
  useEffect(() => {
    loadUsers();
  }, [loadUsers]);

  // While the request is in the air, show a spinner and nothing else.
  if (loading) {
    return (
      <SafeAreaView style={styles.container}>
        <View style={styles.centered}>
          <ActivityIndicator size="large" color={COLORS.accent} />
          <Text style={styles.message}>Loading users...</Text>
        </View>
      </SafeAreaView>
    );
  }

  // If it failed, say so and offer a Retry button instead of a dead screen.
  if (error) {
    return (
      <SafeAreaView style={styles.container}>
        <View style={styles.centered}>
          <Text style={styles.errorTitle}>Something went wrong</Text>
          <Text style={styles.errorText}>{error}</Text>
          <Pressable
            onPress={loadUsers}
            style={({ pressed }) => [styles.button, pressed && styles.buttonPressed]}
          >
            <Text style={styles.buttonText}>Retry</Text>
          </Pressable>
        </View>
      </SafeAreaView>
    );
  }

  // Nobody selected yet, so this is the list screen.
  if (selectedUser === null) {
    // Filter by name as the user types. toLowerCase on both sides means
    // the search does not care about capital letters.
    const term = search.trim().toLowerCase();
    const visibleUsers = term
      ? users.filter((user) => user.name.toLowerCase().includes(term))
      : users;

    return (
      <SafeAreaView style={styles.container}>
        <View style={styles.header}>
          <View>
            <Text style={styles.kicker}>COMP 3074</Text>
            <Text style={styles.title}>Users</Text>
          </View>
          <Pressable
            onPress={loadUsers}
            style={({ pressed }) => [styles.refreshButton, pressed && styles.buttonPressed]}
          >
            <Text style={styles.refreshButtonText}>Refresh</Text>
          </Pressable>
        </View>

        <TextInput
          style={styles.searchInput}
          placeholder="Search by name"
          placeholderTextColor={COLORS.muted}
          value={search}
          onChangeText={setSearch}
          autoCorrect={false}
        />

        <Text style={styles.countLine}>
          {visibleUsers.length} of {users.length} users
        </Text>

        {/* FlatList walks the array and calls renderItem for each object.
            keyExtractor gives React a unique key per row. */}
        <FlatList
          data={visibleUsers}
          keyExtractor={(item) => item.id.toString()}
          renderItem={({ item }) => (
            <UserItem user={item} onPress={() => setSelectedUser(item)} />
          )}
          contentContainerStyle={styles.listContent}
          ListEmptyComponent={
            <Text style={styles.message}>No users match that name.</Text>
          }
        />
      </SafeAreaView>
    );
  }

  // A user is selected, so show the full details. ScrollView is needed
  // because this screen is taller than the phone.
  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={styles.details}>
        {/* Setting selectedUser back to null returns to the list. */}
        <Pressable
          onPress={() => setSelectedUser(null)}
          style={({ pressed }) => [styles.backButton, pressed && styles.buttonPressed]}
        >
          <Text style={styles.backButtonText}>‹ Back to Users</Text>
        </Pressable>

        {/* The same avatar from the list, bigger, as a header for the page. */}
        <View style={styles.detailHeader}>
          <View style={styles.avatarLarge}>
            <Text style={styles.avatarLargeText}>
              {getInitials(selectedUser.name)}
            </Text>
          </View>
          <Text style={styles.detailName}>{selectedUser.name}</Text>
          <Text style={styles.detailUsername}>@{selectedUser.username}</Text>
        </View>

        {/* Contact details get their own accent colour so they stand out
            from the plain text around them. */}
        <Text style={styles.sectionTitle}>Contact</Text>
        <View style={styles.card}>
          <Text style={styles.detailLabel}>ID</Text>
          <Text style={styles.detailValue}>{selectedUser.id}</Text>

          <Text style={styles.detailLabel}>Email</Text>
          <Text style={styles.contactValue}>{selectedUser.email}</Text>

          <Text style={styles.detailLabel}>Phone</Text>
          <Text style={styles.contactValue}>{selectedUser.phone}</Text>

          <Text style={styles.detailLabel}>Website</Text>
          <Text style={styles.contactValue}>{selectedUser.website}</Text>
        </View>

        {/* address is a nested object inside each user. */}
        <Text style={styles.sectionTitle}>Address</Text>
        <View style={styles.card}>
          <DetailRow
            label="Street"
            value={`${selectedUser.address.street}, ${selectedUser.address.suite}`}
          />
          <DetailRow label="City" value={selectedUser.address.city} />
          <DetailRow label="Zip code" value={selectedUser.address.zipcode} />
        </View>

        {/* address.geo is nested one level deeper again, so it gets its
            own small card with the two numbers side by side. */}
        <Text style={styles.sectionTitle}>Coordinates</Text>
        <View style={[styles.card, styles.geoCard]}>
          <View style={styles.geoHalf}>
            <Text style={styles.geoLabel}>LATITUDE</Text>
            <Text style={styles.geoValue}>{selectedUser.address.geo.lat}</Text>
          </View>
          <View style={styles.geoDivider} />
          <View style={styles.geoHalf}>
            <Text style={styles.geoLabel}>LONGITUDE</Text>
            <Text style={styles.geoValue}>{selectedUser.address.geo.lng}</Text>
          </View>
        </View>

        {/* company is the other nested object. */}
        <Text style={styles.sectionTitle}>Company</Text>
        <View style={styles.card}>
          <DetailRow label="Company name" value={selectedUser.company.name} />
          <DetailRow label="Catch phrase" value={selectedUser.company.catchPhrase} />
          <DetailRow label="Business" value={selectedUser.company.bs} />
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: COLORS.background,
    paddingHorizontal: 16,
    // SafeAreaView only leaves room for the status bar on iOS. On Android it
    // behaves like a plain View, so without this the first thing on screen
    // (the Back to Users button) sits underneath the status bar and taps on
    // it go to the system bar instead of the app.
    paddingTop:
      Platform.OS === 'android' ? (StatusBar.currentHeight || 0) + 16 : 16,
  },
  centered: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 24,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-end',
    marginBottom: 14,
  },
  kicker: {
    fontSize: 12,
    fontWeight: '600',
    letterSpacing: 3,
    color: COLORS.accent,
    marginBottom: 4,
  },
  title: {
    fontSize: 30,
    fontWeight: 'bold',
    color: COLORS.heading,
  },
  refreshButton: {
    borderWidth: 1,
    borderColor: COLORS.teal,
    paddingVertical: 8,
    paddingHorizontal: 16,
    borderRadius: 20,
  },
  refreshButtonText: {
    color: COLORS.teal,
    fontSize: 14,
    fontWeight: '600',
  },
  searchInput: {
    backgroundColor: COLORS.card,
    color: COLORS.heading,
    borderRadius: 12,
    paddingVertical: 12,
    paddingHorizontal: 16,
    fontSize: 16,
  },
  countLine: {
    color: COLORS.muted,
    fontSize: 13,
    marginTop: 10,
    marginBottom: 6,
    letterSpacing: 1,
  },
  listContent: {
    paddingBottom: 24,
  },
  userItem: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: COLORS.card,
    padding: 14,
    marginBottom: 10,
    borderRadius: 14,
  },
  userItemPressed: {
    backgroundColor: COLORS.cardSoft,
  },
  userItemText: {
    flex: 1,
    marginLeft: 14,
  },
  avatar: {
    width: 46,
    height: 46,
    borderRadius: 23,
    backgroundColor: COLORS.accent,
    justifyContent: 'center',
    alignItems: 'center',
  },
  avatarText: {
    color: COLORS.onAccent,
    fontSize: 16,
    fontWeight: 'bold',
  },
  userName: {
    fontSize: 17,
    fontWeight: '600',
    color: COLORS.heading,
  },
  userEmail: {
    fontSize: 13,
    color: COLORS.muted,
    marginTop: 3,
  },
  chevron: {
    fontSize: 26,
    color: COLORS.muted,
    marginLeft: 8,
  },
  message: {
    marginTop: 12,
    fontSize: 16,
    color: COLORS.body,
    textAlign: 'center',
  },
  errorTitle: {
    fontSize: 22,
    fontWeight: 'bold',
    color: COLORS.heading,
    marginBottom: 8,
  },
  errorText: {
    fontSize: 16,
    color: COLORS.error,
    textAlign: 'center',
  },
  button: {
    marginTop: 22,
    backgroundColor: COLORS.accent,
    paddingVertical: 12,
    paddingHorizontal: 32,
    borderRadius: 24,
  },
  buttonPressed: {
    opacity: 0.7,
  },
  buttonText: {
    color: COLORS.onAccent,
    fontSize: 15,
    fontWeight: '700',
  },
  details: {
    paddingBottom: 40,
  },
  backButton: {
    alignSelf: 'flex-start',
    backgroundColor: COLORS.accentDeep,
    paddingVertical: 10,
    paddingHorizontal: 16,
    borderRadius: 20,
    marginBottom: 20,
  },
  backButtonText: {
    color: COLORS.heading,
    fontSize: 14,
    fontWeight: '700',
  },
  detailHeader: {
    alignItems: 'center',
    marginBottom: 10,
  },
  avatarLarge: {
    width: 84,
    height: 84,
    borderRadius: 42,
    backgroundColor: COLORS.accent,
    justifyContent: 'center',
    alignItems: 'center',
  },
  avatarLargeText: {
    color: COLORS.onAccent,
    fontSize: 30,
    fontWeight: 'bold',
  },
  detailName: {
    fontSize: 25,
    fontWeight: 'bold',
    color: COLORS.heading,
    marginTop: 14,
    textAlign: 'center',
  },
  detailUsername: {
    fontSize: 15,
    color: COLORS.teal,
    marginTop: 4,
  },
  sectionTitle: {
    fontSize: 13,
    fontWeight: '700',
    letterSpacing: 2,
    color: COLORS.accent,
    marginTop: 24,
    marginBottom: 10,
  },
  card: {
    backgroundColor: COLORS.card,
    borderRadius: 14,
    padding: 16,
  },
  detailRow: {
    marginBottom: 2,
  },
  detailLabel: {
    fontSize: 12,
    fontWeight: '600',
    letterSpacing: 1,
    color: COLORS.muted,
    marginTop: 12,
  },
  detailValue: {
    fontSize: 16,
    color: COLORS.body,
    marginTop: 3,
  },
  contactValue: {
    fontSize: 16,
    color: COLORS.teal,
    fontWeight: '600',
    marginTop: 3,
  },
  geoCard: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  geoHalf: {
    flex: 1,
    alignItems: 'center',
  },
  geoDivider: {
    width: 1,
    height: 44,
    backgroundColor: COLORS.cardSoft,
  },
  geoLabel: {
    fontSize: 11,
    fontWeight: '700',
    letterSpacing: 2,
    color: COLORS.muted,
  },
  geoValue: {
    fontSize: 19,
    fontWeight: 'bold',
    color: COLORS.heading,
    marginTop: 6,
  },
});
