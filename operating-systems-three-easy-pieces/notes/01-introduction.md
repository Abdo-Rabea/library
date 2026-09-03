# Chapter 2: Introduction to Operating Systems — Summary Notes

## The Basic Model

- A running program just **executes instructions**: fetch → decode → execute, repeated millions/billions of times per second (Von Neumann model).
- The OS is the software layer that makes it **easy to run programs**, share memory, use devices, etc.

## What the OS Does — Three Core Roles

1. **Virtual Machine** — turns physical resources (CPU, memory, disk) into easier, more powerful virtual versions.
2. **Provides APIs** — exposes system calls (~hundreds) so programs can request services; sometimes called a "standard library."
3. **Resource Manager** — manages CPU, memory, disk sharing across programs (efficiently, fairly, etc.).

---

## 2.1 Virtualizing the CPU

- Running one program on one CPU is simple — it just executes.
- Running **many programs "at once"** on a single CPU is an illusion created by the OS + hardware: this is **CPU virtualization**.
- Raises a **policy question**: which program runs when? (mechanism vs. policy is a recurring theme).

## 2.2 Virtualizing Memory

- Physical memory = simple array of bytes (address → read/write).
- Each process gets its own **private virtual address space**, mapped by the OS onto physical memory.
- Proof: two instances of the same program both "see" the same address (e.g., `0x200000`) but hold independent values — because each has its own virtualized memory.

## 2.3 Concurrency

- Concurrency = problems that arise from doing many things at once **within the same memory space** (originally an OS problem, now also an application/multithreading problem).
- Example: two threads incrementing a shared `counter` produce **wrong, non-deterministic results** at high iteration counts.
- Root cause: `counter++` is not atomic — it's actually 3 instructions (load, increment, store). Interleaving of these across threads causes lost updates.
- **Crux:** How do we build correct programs when many threads run concurrently in shared memory? What OS/hardware primitives are needed?

## 2.4 Persistence

- Memory (DRAM) is **volatile** — data is lost on crash/power-off. Need durable storage.
- Hardware: I/O devices (HDDs, SSDs).
- Software: the **file system**, part of the OS, manages files reliably/efficiently on disk.
- Unlike CPU/memory, disk is **not** privately virtualized per process — files are meant to be **shared** (e.g., editor → compiler → executable pipeline).
- Example program: `open()` → `write()` → `close()` — these are system calls routed to the file system.
- File systems must handle:
  - Deciding where data goes on disk, tracking it in metadata structures.
  - **Crash consistency** techniques (journaling, copy-on-write).
  - Performance (batching writes) and efficient data structures (lists, B-trees).
- **Crux:** How to store data persistently, correctly, and with high performance, despite failures?

---

## 2.5 Design Goals of an OS

- **Abstraction** — build convenient, understandable layers (fundamental to all of CS).
- **Performance** — minimize OS overhead (extra time/space).
- **Protection / Isolation** — processes shouldn't harm each other or the OS; isolation is central to protection.
- **Reliability** — OS must run non-stop; if it fails, everything fails.
- Other goals: energy efficiency, security, mobility — vary by context/device.
