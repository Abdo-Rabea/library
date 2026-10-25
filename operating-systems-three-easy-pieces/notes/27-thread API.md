# Chapter 27: Interlude — Thread API

> **Crux: How to create and control threads?**
> What interfaces should the OS present for thread creation and control? How should they be designed for ease of use as well as utility?

## Overview

- This chapter is a **reference** for the main parts of the POSIX thread API (pthreads).
- Later chapters introduce locks and condition variables more slowly, with many examples.

## 27.1 Thread Creation

```c
int pthread_create(pthread_t *thread, const pthread_attr_t *attr,
                   void *(*start_routine)(void*), void *arg);
```

- **`thread`**: pointer to a `pthread_t` structure, initialized by the call and used later to interact with the thread (e.g., join).
- **`attr`**: thread attributes (stack size, scheduling priority, etc.).
  - Initialized with `pthread_attr_init()`.
  - Usually pass **`NULL`** for defaults.
- **`start_routine`**: function pointer saying which function the thread starts running.
  - Takes one `void *` argument and returns a `void *`.
- **`arg`**: the argument passed to `start_routine`.
- **Why `void *`?** It lets you pass **any type** of argument and return **any type** of result.
- To pass several values, **pack them into a struct** (e.g., `myarg_t`). The thread casts `arg` back to the expected type.
- A created thread is a live execution entity with **its own call stack**, in the **same address space** as the other threads.

## 27.2 Thread Completion

```c
int pthread_join(pthread_t thread, void **value_ptr);
```

- Waits for a specific thread to finish.
- **`thread`**: the `pthread_t` set by `pthread_create()`.
- **`value_ptr`**: pointer to the place that receives the thread's return value.
  - It is a **pointer to a pointer**, because join modifies the value it is given.
- **Shortcuts:**
  - Pass `NULL` as the thread argument if there are no arguments.
  - Pass `NULL` to join if you don't need the return value.
  - A single value (e.g., an `int`) can be passed or returned by casting, with no struct needed.
- **Danger: never return a pointer to something allocated on the thread's stack.**
  - The stack frame is deallocated when the function returns, so the pointer dangles and gives garbage or undefined behavior.
  - Return heap-allocated data instead (e.g., `malloc`, then `free` it in the caller).
  - `gcc` warnings often catch this, so **pay attention to compiler warnings**.
- **Create-then-join immediately is pointless**, since that is just a procedure call. Threads are useful when several run at once.
- **Not all programs need join:**
  - Long-lived programs like web servers (main thread accepts requests and hands them to workers) may never join.
  - Parallel programs that split a task use join to make sure all work finishes before moving on.

## 27.3 Locks

```c
int pthread_mutex_lock(pthread_mutex_t *mutex);
int pthread_mutex_unlock(pthread_mutex_t *mutex);
```

- Provide **mutual exclusion** around a critical section.
- **Usage:** lock, run the critical section (e.g., `x = x + 1`), unlock.
- **Semantics:**
  - If no one holds the lock, the caller acquires it and enters.
  - If another thread holds it, the caller **blocks inside `lock()`** until it can acquire it.
  - Only the holder should call unlock.
- **Two common bugs in the naive code:**
  1. **No initialization.** Locks must be initialized before use, in one of two ways:
     - Static: `pthread_mutex_t lock = PTHREAD_MUTEX_INITIALIZER;`
     - Dynamic: `pthread_mutex_init(&lock, NULL);` (usually preferred).
     - Call `pthread_mutex_destroy()` when done with a dynamically initialized lock.
  2. **No error-code checking.** Lock and unlock can fail, and an unchecked failure can silently let multiple threads into the critical section.
     - Minimum: use **wrappers that `assert(rc == 0)`**, which is fine only if exiting on failure is acceptable.
     - Real programs should check and handle failures properly.
- **Other acquisition routines** (generally avoid, but useful for not getting stuck, e.g., with deadlock):
  - `pthread_mutex_trylock()` returns failure if the lock is already held.
  - `pthread_mutex_timedlock()` returns after a timeout or acquisition, whichever comes first.
  - A timeout of 0 makes it behave like `trylock`.

## 27.4 Condition Variables

```c
int pthread_cond_wait(pthread_cond_t *cond, pthread_mutex_t *mutex);
int pthread_cond_signal(pthread_cond_t *cond);
```

- Used for **signaling between threads** when one must wait for another to do something.
- Requires an **associated lock**, which must be **held** when calling wait or signal.
- **`wait`** puts the caller to sleep until another thread signals.
- **Waiting pattern:**
  ```c
  Pthread_mutex_lock(&lock);
  while (ready == 0)
      Pthread_cond_wait(&cond, &lock);
  Pthread_mutex_unlock(&lock);
  ```
- **Signaling pattern:**
  ```c
  Pthread_mutex_lock(&lock);
  ready = 1;
  Pthread_cond_signal(&cond);
  Pthread_mutex_unlock(&lock);
  ```
- **Why the lock is held while signaling and changing `ready`:** to avoid introducing a race condition.
- **Why `wait` takes the lock but `signal` doesn't:**
  - `wait` **releases the lock** while putting the thread to sleep. Otherwise nobody else could acquire it to signal.
  - Before returning after wakeup, `wait` **re-acquires the lock**. So the thread holds the lock whenever it is running between the start of the wait sequence and the unlock.
- **Why `while` instead of `if`:**
  - Some implementations can wake a thread **spuriously**.
  - Treat a wakeup as a **hint** that something might have changed, not as proof.
  - Rechecking the condition is the safe approach.
- **Never use a plain flag to signal between threads** (`while (ready == 0) ; // spin`, then `ready = 1;`):
  1. **Poor performance**: spinning wastes CPU cycles.
  2. **Error-prone**: a study found roughly **half** of such ad hoc synchronizations were buggy.
- Condition variables can also be initialized dynamically with `pthread_cond_init()` and `pthread_cond_destroy()`, but that is more work than `PTHREAD_COND_INITIALIZER`.

## 27.5 Compiling and Running

- Include **`pthread.h`** in your code.
- Link with the **`-pthread`** flag:
  ```
  gcc -o main main.c -Wall -pthread
  ```
- Compiling successfully doesn't mean the program is correct.

## 27.6 Summary

- Covered: **thread creation** (`pthread_create`), **join**, **locks** for mutual exclusion, and **condition variables** for signaling and waiting.
- **The hard part is not the API but the logic of building correct concurrent programs.**
- More API: `man -k pthread` lists over 100 routines.

## Aside: Thread API Guidelines

- **Keep it simple.** Tricky thread interactions cause bugs.
- **Minimize thread interactions.** Use tried-and-true approaches for each one.
- **Initialize locks and condition variables.** Otherwise the code fails in strange, intermittent ways.
- **Check return codes** on every call.
- **Be careful passing arguments and returning values.** Passing a reference to a stack variable is probably a mistake.
- **Each thread has its own stack.**
  - Local variables are private to that thread.
  - To share data, put it on the **heap** or in a globally accessible place.
- **Always use condition variables to signal between threads**, not simple flags.
- **Use the man pages.** The Linux pthread man pages are detailed and informative.
