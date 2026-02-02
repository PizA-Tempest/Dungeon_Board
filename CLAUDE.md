# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Dungeon Board is a turn-based fantasy board game RPG inspired by Dungeons & Dragons, Monopoly, and party games. Players move around a dungeon-themed board, roll dice, face monsters, trigger events, and grow stronger through classes, races, items, and choices.

**Current Status**: This is a greenfield project - no code exists yet. Only design documentation is present.

## Development Approach

This project follows a **vibe-first, MVP-driven approach**: fun before features, experience before systems.

### Core Principles
- Fun > realism
- Clarity > complexity
- Chaos is a feature, not a bug
- Systems must support player stories
- Every mechanic should create a decision or emotion

### When Starting Work
**ENTER PLAN MODE** before writing any code:
- Outline the core game loop
- Define MVP mechanics only
- List required systems (board, turn, combat, AI)
- Identify what can be simplified or cut
- Propose clear development steps

Only proceed to coding after the plan is agreed upon.

## MVP Requirements

The game must include:
- **User authentication** - players must log in before playing
- **Room system** - create or join rooms (private rooms for friends)
- **Multiplayer support** - 1-4 players per room (friends or bots)
- **Board-based movement** - dungeon-themed board with dice rolls
- **Character system** - 8 classes and 8 races (mix & match)
- **Core gameplay** - combat, events, loot, and progression
- **Session design** - completable in a single play session

## Target Experience

Players should feel:
- Excited by randomness and dice rolls
- Powerful when builds come together
- Tense when rivals or bots gain advantage
- Amused by chaos, betrayal, and lucky moments
- Strategic but never overwhelmed

## Technical Context

This is a Java project. The technology stack has not been established yet.

When proposing technical decisions:
- Choose frameworks that accelerate MVP delivery
- Avoid over-engineering and premature optimization
- Prioritize player-visible features over technical elegance
- Keep the architecture simple enough to understand quickly

## Important Constraints

- Sessions should be **short, punchy, and replayable**, not long or exhausting
- Players must understand how to play without tutorials
- Each match should tell a slightly different story
- Avoid deep math-heavy RPG systems
- Avoid long setup or learning time
