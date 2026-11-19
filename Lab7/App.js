// COMP3074 - React Native Lab: Expo Snack, Components and Styling
// Travis Eweka - 101583426

import React, { useState } from 'react';
import { Pressable, StyleSheet, Text, View } from 'react-native';

// Two colour sets. The button below swaps between them, which shows the
// styling changing without reloading the Snack.
const THEMES = {
  night: {
    background: '#0F1115',
    card: '#1A1D25',
    accent: '#F4A261',
    name: '#F5F3EF',
    id: '#9BA1AC',
    buttonText: '#0F1115',
  },
  day: {
    background: '#F3EFE7',
    card: '#FFFFFF',
    accent: '#2A9D8F',
    name: '#1B1F24',
    id: '#5C6673',
    buttonText: '#FFFFFF',
  },
};

export default function App() {
  const [themeName, setThemeName] = useState('night');
  const theme = THEMES[themeName];

  function toggleTheme() {
    setThemeName(themeName === 'night' ? 'day' : 'night');
  }

  return (
    // POSITION: flex:1 fills the screen, and the content sits in the
    // middle vertically (justifyContent) and horizontally (alignItems).
    <View style={[styles.screen, { backgroundColor: theme.background }]}>
      <View style={[styles.card, { backgroundColor: theme.card }]}>
        {/* A thin coloured bar across the top of the card. */}
        <View style={[styles.accentBar, { backgroundColor: theme.accent }]} />

        {/* A small label above the name, spaced out with letterSpacing. */}
        <Text style={[styles.label, { color: theme.accent }]}>
          COMP 3074
        </Text>

        {/* SIZE: the name is 34, much larger than everything else. */}
        <Text style={[styles.name, { color: theme.name }]}>
          Travis Eweka
        </Text>

        {/* SPACING: marginTop pushes the ID away from the name. */}
        <Text style={[styles.studentId, { color: theme.id }]}>
          Student ID: 101583426
        </Text>

        {/* Optional challenge: an extra component, styled to match. */}
        <Pressable
          onPress={toggleTheme}
          style={({ pressed }) => [
            styles.button,
            { backgroundColor: theme.accent, opacity: pressed ? 0.7 : 1 },
          ]}
        >
          <Text style={[styles.buttonText, { color: theme.buttonText }]}>
            {themeName === 'night' ? 'Switch to light' : 'Switch to dark'}
          </Text>
        </Pressable>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  screen: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 24,
  },
  card: {
    width: '88%',
    paddingVertical: 32,
    paddingHorizontal: 24,
    borderRadius: 20,
    alignItems: 'center',
    overflow: 'hidden',
  },
  accentBar: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    height: 6,
  },
  label: {
    fontSize: 13,
    fontWeight: '600',
    letterSpacing: 3,
    marginBottom: 14,
  },
  name: {
    fontSize: 34,
    fontWeight: 'bold',
    textAlign: 'center',
  },
  studentId: {
    fontSize: 18,
    marginTop: 12,
    letterSpacing: 1,
  },
  button: {
    marginTop: 28,
    paddingVertical: 12,
    paddingHorizontal: 28,
    borderRadius: 24,
  },
  buttonText: {
    fontSize: 15,
    fontWeight: '600',
  },
});
