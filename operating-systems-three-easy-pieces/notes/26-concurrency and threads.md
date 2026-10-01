# Chapter 26: Concurrency — An Introduction

## Overview

- So far: the OS virtualizes the **CPU** (processes) and **memory** (address spaces).
- New abstraction: the **thread**, a single point of execution inside a process.
- A multi-threaded program has **multiple PCs**, so more than one point of execution at once.
- A thread is like a separate process, except that **threads share the same address space** and can access the same data.

## Threads vs. Processes

- **Thread state:** its own **PC** and its own **private registers**.
- **Context switch between threads (T1 → T2):** save T1's registers, restore T2's, same as with processes.
  - Processes save state to a **PCB**; threads save to a **TCB** (thread control block).
  - **Key difference:** the address space stays the same, so there is **no page-table switch**.
- **Stacks:**
  - A single-threaded process has **one stack**.
  - A multi-threaded process has **one stack per thread**.
  - Stack variables, parameters and return values live in **thread-local storage** (that thread's stack).
  - This breaks the clean layout where stack and heap grow independently. It is usually fine because stacks are small, except with heavy recursion.

## 26.1 Why Use Threads?

1. **Parallelism**
   - Splitting work (e.g., adding large arrays) across multiple CPUs speeds it up.
   - Turning a single-threaded program into one that uses multiple CPUs is **parallelization**; the typical approach is one thread per CPU.
2. **Avoiding blocking on slow I/O**
   - While one thread waits on I/O (network, disk, page fault), the scheduler runs other threads.
   - This **overlaps I/O with other work**, like multiprogramming but within one program.
   - Web servers and DBMSs rely on this.

- **Threads vs. processes:**
  - Threads share an address space, so sharing data is easy.
  - Processes are better for logically separate tasks with little shared memory.

## 26.2 Thread Creation (Example)

- The main thread creates T1 and T2 with `pthread_create()`, then calls `pthread_join()` on each to wait for them to finish.
- Three threads in total: main, T1, T2.
- **Creation is like a function call, but the new thread runs independently.** It may run immediately or much later.
- **Execution order is decided by the scheduler and is not predictable.** Possible orderings:
  - T1 and T2 run only after main blocks on join.
  - T1 runs right after its creation, then T2.
  - T2 runs before T1, even though T1 was created first.
- Being created first does **not** mean running first.
- Even this simple case is hard to reason about, and concurrency makes it much worse.

## 26.3 Why It Gets Worse: Shared Data

- Example: two threads each do `counter = counter + 1` **10 million times** on a shared global. The expected result is **20,000,000**.
- Actual results (even on a single CPU): sometimes correct, but often wrong (e.g., 19,345,221 or 19,221,041) and **different on every run**.
- **Tip: know and use your tools.**
  - A disassembler like `objdump -d` shows the assembly your code compiles to.
  - Also learn `gdb` and memory profilers like `valgrind`.

## 26.4 The Heart of the Problem: Uncontrolled Scheduling

- `counter = counter + 1` compiles to **three instructions**:
  1. `mov counter, %eax` (load)
  2. `add $1, %eax` (increment)
  3. `mov %eax, counter` (store)
- **Bad interleaving (counter starts at 50):**
  - T1 loads 50 and increments to 51 in `eax`. A **timer interrupt** hits, and T1's state is saved to its TCB.
  - T2 runs the whole sequence: loads 50, increments to 51, stores 51.
  - T1 resumes and stores its stale 51.
  - Result: the code ran twice but counter is **51 instead of 52** (a lost update).
- Registers are **private per thread** (virtualized by the context switch), but memory is **shared**.
- **Definitions:**
  - **Race condition (data race):** the result depends on the timing of execution.
  - **Indeterminate program:** output varies from run to run instead of being deterministic.
  - **Critical section:** code that accesses a shared resource and must not be executed concurrently by more than one thread.
  - **Mutual exclusion:** if one thread is in the critical section, all others are kept out.
- These terms were coined by **Dijkstra**.

## 26.5 The Wish for Atomicity

- **Ideal fix:** a single hardware instruction, e.g. `memory-add addr, $1`, that does the whole update **atomically**.
  - The hardware guarantees an interrupt sees the instruction either not run or fully run, never partway.
  - **Atomic** means "as a unit" or **"all or nothing"**.
- **Problem:** we can't have a special instruction for everything (e.g., an "atomic B-tree update").
- **Real approach:** the hardware provides a few useful instructions, and with OS help we build general **synchronization primitives** on top. These make critical sections run in a controlled way and give correct results.
- **Tip: use atomic operations.**
  - Atomicity is a powerful technique across architecture, concurrent code, file systems (journaling, copy-on-write), databases and distributed systems.
  - A group of actions made atomic is sometimes called a **transaction**.

> **Crux: How to support synchronization?**
> What support is needed from the hardware and the OS to build useful synchronization primitives? How can we build them correctly and efficiently, and how can programs use them to get the desired results?

## 26.6 One More Problem: Waiting for Another

- A second kind of interaction: **one thread must wait for another to finish an action** before continuing.
  - Example: a process sleeps during disk I/O and must be woken up when the I/O completes.
- So later chapters cover two things:
  1. Primitives for **atomicity** (critical sections).
  2. Mechanisms for **sleeping/waking** (covered in the condition variables chapter).

## 26.7 Summary: Why in OS Class?

- **Historical reason:** the OS was the **first concurrent program**, and many concurrency techniques were invented for it.
- Interrupts can occur at any time, so the OS's own shared structures (page tables, process lists, file system structures, bitmaps, inodes, etc.) are critical sections needing synchronization.
- **Example:** two processes appending to the same file must each allocate a block, update the inode, and update the file size. An untimely interrupt here causes the same race problems as the counter.
- Later, multi-threaded applications made these issues relevant to application programmers too.

## Key Concurrency Terms (Aside)

- **Critical section:** code that accesses a shared resource (variable or data structure).
- **Race condition (data race):** multiple threads enter the critical section at about the same time and both update shared data, giving a surprising result.
- **Indeterminate program:** contains one or more race conditions, so its output varies between runs.
- **Mutual exclusion:** primitives that ensure only one thread is in the critical section at a time. This avoids races and gives deterministic output.
