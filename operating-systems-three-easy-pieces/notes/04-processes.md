# Chapter 4: The Abstraction — The Process

## Overview

- A **process** = a running program. The program itself is just static bytes on disk; the OS brings it to life by loading and executing it.
- Motivation: users want to run **many programs at once** (browser, mail, music, etc.), so the OS must support this seamlessly.

## The Process Abstraction & Virtualizing the CPU

> **Crux: How to provide the illusion of many CPUs?**
> Given only a few physical CPUs, how can the OS make it seem like there's a nearly endless supply of them?

- Solved via **CPU virtualization**: the OS runs one process, stops it, runs another, and so on — this is **time sharing** of the CPU.
- Cost of time sharing: each process runs more slowly since the CPU is shared.
- Two building blocks for virtualization:
  - **Mechanisms** — low-level _how_ (e.g., how does a context switch work?).
  - **Policies** — high-level _which_ (e.g., which process should run next? → scheduling policy, uses history/workload/performance goals).
  - **Design principle:** separate policy from mechanism → modularity, lets you change scheduling algorithms without rewriting low-level machinery.
- **Tip:** Time sharing (resource used briefly by many in turn) vs. **space sharing** (resource divided among users, e.g., disk blocks assigned per file).

## 4.1 What Constitutes a Process (Machine State)

A process = the inventory of everything it can read/write at a given instant:

- **Memory (address space)** — holds instructions and data the process reads/writes.
- **Registers** — including special ones:
  - **Program counter (PC)/instruction pointer** — which instruction executes next.
  - **Stack pointer / frame pointer** — manage function calls, locals, return addresses.
- **I/O info** — e.g., list of currently open files.

## 4.2 Process API (basic operations any OS must support)

- **Create** — start a new process (e.g., running a shell command).
- **Destroy** — forcibly kill a runaway process.
- **Wait** — block until a process finishes.
- **Miscellaneous Control** — e.g., suspend/resume.
- **Status** — query info like runtime or current state.

## 4.3 Process Creation — How a Program Becomes a Process

1. **Load code + static data** from disk into the process's address space.
   - _Eager loading_ (older OSes): load everything upfront.
   - _Lazy loading_ (modern OSes): load pieces only as needed (relies on paging/swapping, covered later).
2. **Allocate the run-time stack** — used for local variables, function params, return addresses; OS initializes it with `argc`/`argv` for `main()`.
3. **Allocate the heap** — for dynamically requested memory (`malloc()`/`free()`); starts small, grows as the program requests more.
4. **I/O initialization** — e.g., in UNIX, every process gets 3 default file descriptors: stdin, stdout, stderr.
5. **Start execution** — OS jumps to the program's entry point (`main()`), handing control to the new process.

## 4.4 Process States

A process is in one of three basic states:

- **Running** — actively executing instructions.
- **Ready** — could run, but OS hasn't scheduled it right now.
- **Blocked** — waiting on some event (e.g., I/O completion) before it can run again.

**Transitions:**

- Ready → Running = **scheduled**
- Running → Ready = **descheduled**
- Blocked → Ready happens when the awaited event (e.g., I/O done) completes.

**Example traces:**

- _CPU-only processes:_ Process0 runs fully, then Process1 runs fully (no blocking needed).
- _CPU + I/O:_ Process0 runs, initiates I/O → becomes **Blocked** → OS runs Process1 to keep CPU busy → Process0's I/O completes → moves to **Ready** (not immediately Running) → eventually resumes.
- Key point: the OS scheduler makes real decisions here (e.g., whether to preempt Process1 back to Process0 once I/O finishes) — a topic covered later.

## 4.5 Data Structures

- OS tracks all processes via a **process list** (task list).
- Each entry = a **Process Control Block (PCB)** / process descriptor — a struct holding all process metadata.
- Example from **xv6** kernel (`struct proc`): tracks memory pointers, size, kernel stack, **state** (enum: `UNUSED, EMBRYO, SLEEPING, RUNNABLE, RUNNING, ZOMBIE`), PID, parent pointer, open files, current directory, saved **register context** (for context switching), and trap frame.
- **Register context**: when a process is stopped, its register values are saved here; restoring them resumes the process (this mechanism = **context switch**).
- Extra states beyond running/ready/blocked:
  - **Initial state** — process being created.
  - **Zombie state** (final state) — process has exited but not yet cleaned up; lets the parent inspect its exit/return code via `wait()`, after which the OS can free its data structures.

## Key Terms (Aside)

- **Process** — the OS's abstraction of a running program (state = memory, registers, I/O info).
- **Process API** — create, destroy, wait, and other process-related calls.
- **Process states** — running, ready, blocked (+ initial/zombie).
- **Process list / PCB** — data structure(s) tracking all processes and their info.

## 4.6 Summary

- The process is the OS's most fundamental abstraction of a running program.
- Understanding it sets up the next topics: the **mechanisms** (e.g., context switches) and **policies** (e.g., scheduling) needed to actually implement and manage CPU virtualization.
